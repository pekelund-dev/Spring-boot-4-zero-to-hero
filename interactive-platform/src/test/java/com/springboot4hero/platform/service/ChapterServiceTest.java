package com.springboot4hero.platform.service;

import com.springboot4hero.platform.model.Chapter;
import com.springboot4hero.platform.repository.ChapterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChapterServiceTest {

    @Mock
    private ChapterRepository chapterRepository;

    @InjectMocks
    private ChapterService chapterService;

    private Chapter chapter1;
    private Chapter chapter2;

    @BeforeEach
    void setUp() {
        chapter1 = Chapter.builder()
                .id(1L)
                .chapterId("01-introduction")
                .title("Introduction to Spring Boot 4.x")
                .summary("Learn about Spring Boot fundamentals")
                .orderIndex(1)
                .available(true)
                .build();

        chapter2 = Chapter.builder()
                .id(2L)
                .chapterId("02-setup")
                .title("Setting Up Your Development Environment")
                .summary("Install and configure tools")
                .orderIndex(2)
                .available(true)
                .build();
    }

    @Test
    void testGetAllChapters() {
        // Arrange
        when(chapterRepository.findAllByOrderByOrderIndex())
                .thenReturn(Arrays.asList(chapter1, chapter2));

        // Act
        List<Chapter> chapters = chapterService.getAllChapters();

        // Assert
        assertNotNull(chapters);
        assertEquals(2, chapters.size());
        assertEquals("01-introduction", chapters.get(0).getChapterId());
        assertEquals("02-setup", chapters.get(1).getChapterId());
        verify(chapterRepository, times(1)).findAllByOrderByOrderIndex();
    }

    @Test
    void testGetAvailableChapters() {
        // Arrange
        when(chapterRepository.findByAvailableOrderByOrderIndex(true))
                .thenReturn(Arrays.asList(chapter1, chapter2));

        // Act
        List<Chapter> chapters = chapterService.getAvailableChapters();

        // Assert
        assertNotNull(chapters);
        assertEquals(2, chapters.size());
        assertTrue(chapters.stream().allMatch(Chapter::isAvailable));
        verify(chapterRepository, times(1)).findByAvailableOrderByOrderIndex(true);
    }

    @Test
    void testGetChapterById_Found() {
        // Arrange
        when(chapterRepository.findByChapterId("01-introduction"))
                .thenReturn(Optional.of(chapter1));

        // Act
        Optional<Chapter> result = chapterService.getChapterById("01-introduction");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("01-introduction", result.get().getChapterId());
        assertEquals("Introduction to Spring Boot 4.x", result.get().getTitle());
        verify(chapterRepository, times(1)).findByChapterId("01-introduction");
    }

    @Test
    void testGetChapterById_NotFound() {
        // Arrange
        when(chapterRepository.findByChapterId("99-nonexistent"))
                .thenReturn(Optional.empty());

        // Act
        Optional<Chapter> result = chapterService.getChapterById("99-nonexistent");

        // Assert
        assertFalse(result.isPresent());
        verify(chapterRepository, times(1)).findByChapterId("99-nonexistent");
    }
}
