package com.springboot4hero.platform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot4hero.platform.model.Chapter;
import com.springboot4hero.platform.repository.ChapterRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class ChapterService {

    private static final Logger logger = LoggerFactory.getLogger(ChapterService.class);
    private static final Pattern CHAPTER_DIR_PATTERN = Pattern.compile("^(\\d+)-(.+)$");

    @Autowired
    private ChapterRepository chapterRepository;

    @Value("${content.path}")
    private String contentPath;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Get all available chapters ordered by index
     */
    public List<Chapter> getAllChapters() {
        return chapterRepository.findAllByOrderByOrderIndex();
    }

    /**
     * Get all available chapters (not hidden/disabled)
     */
    public List<Chapter> getAvailableChapters() {
        return chapterRepository.findByAvailableOrderByOrderIndex(true);
    }

    /**
     * Get a specific chapter by ID
     */
    public Optional<Chapter> getChapterById(String chapterId) {
        return chapterRepository.findByChapterId(chapterId);
    }

    /**
     * Load chapters from filesystem
     */
    @PostConstruct
    @Transactional
    public void loadChaptersFromFilesystem() {
        logger.info("Loading chapters from filesystem: {}", contentPath);
        
        Path chaptersPath = Paths.get(contentPath).toAbsolutePath().normalize();
        File chaptersDir = chaptersPath.toFile();

        if (!chaptersDir.exists() || !chaptersDir.isDirectory()) {
            logger.error("Chapters directory does not exist: {}", chaptersPath);
            return;
        }

        List<Chapter> loadedChapters = new ArrayList<>();

        try (Stream<Path> paths = Files.list(chaptersPath)) {
            paths.filter(Files::isDirectory)
                 .forEach(chapterPath -> {
                     File chapterDir = chapterPath.toFile();
                     String dirName = chapterDir.getName();
                     
                     Matcher matcher = CHAPTER_DIR_PATTERN.matcher(dirName);
                     if (matcher.matches()) {
                         try {
                             int orderIndex = Integer.parseInt(matcher.group(1));
                             String chapterId = dirName;
                             
                             // Read metadata
                             File metadataFile = new File(chapterDir, "metadata.json");
                             ChapterMetadata metadata = readMetadata(metadataFile, chapterId);
                             
                             // Check if chapter exists in database
                             Optional<Chapter> existingChapter = chapterRepository.findByChapterId(chapterId);
                             
                             Chapter chapter;
                             if (existingChapter.isPresent()) {
                                 // Update existing chapter
                                 chapter = existingChapter.get();
                                 chapter.setTitle(metadata.title);
                                 chapter.setSummary(metadata.summary);
                                 chapter.setOrderIndex(orderIndex);
                                 chapter.setAvailable(true);
                             } else {
                                 // Create new chapter
                                 chapter = Chapter.builder()
                                         .chapterId(chapterId)
                                         .title(metadata.title)
                                         .summary(metadata.summary)
                                         .orderIndex(orderIndex)
                                         .available(true)
                                         .build();
                             }
                             
                             Chapter savedChapter = chapterRepository.save(chapter);
                             loadedChapters.add(savedChapter);
                             logger.info("Loaded chapter: {} - {}", chapterId, metadata.title);
                             
                         } catch (Exception e) {
                             logger.error("Error loading chapter from directory: {}", dirName, e);
                         }
                     }
                 });
            
            logger.info("Successfully loaded {} chapters", loadedChapters.size());
            
        } catch (IOException e) {
            logger.error("Error reading chapters directory", e);
        }
    }

    /**
     * Read metadata from metadata.json file
     */
    private ChapterMetadata readMetadata(File metadataFile, String chapterId) {
        if (metadataFile.exists()) {
            try {
                JsonNode jsonNode = objectMapper.readTree(metadataFile);
                String title = jsonNode.has("title") ? jsonNode.get("title").asText() : generateTitleFromId(chapterId);
                String summary = jsonNode.has("summary") ? jsonNode.get("summary").asText() : "";
                return new ChapterMetadata(title, summary);
            } catch (IOException e) {
                logger.warn("Error reading metadata file for {}, using defaults", chapterId, e);
            }
        }
        
        // Return default metadata if file doesn't exist or can't be read
        return new ChapterMetadata(generateTitleFromId(chapterId), "");
    }

    /**
     * Generate a title from chapter ID (e.g., "01-introduction" -> "Introduction")
     */
    private String generateTitleFromId(String chapterId) {
        Matcher matcher = CHAPTER_DIR_PATTERN.matcher(chapterId);
        if (matcher.matches()) {
            String name = matcher.group(2);
            // Convert kebab-case to Title Case
            return Stream.of(name.split("-"))
                    .filter(word -> !word.isEmpty()) // Filter out empty strings
                    .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                    .reduce((a, b) -> a + " " + b)
                    .orElse(name);
        }
        return chapterId;
    }

    /**
     * Inner class to hold metadata
     */
    private static class ChapterMetadata {
        final String title;
        final String summary;

        ChapterMetadata(String title, String summary) {
            this.title = title;
            this.summary = summary;
        }
    }
}
