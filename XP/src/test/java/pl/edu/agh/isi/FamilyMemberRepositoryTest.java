package pl.edu.agh.isi;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Family Member Repository")
class FamilyMemberRepositoryTest {

    @TempDir
    Path tempDir;
    
    private File testFile;
    private FamilyMemberRepository repository;
    
    @BeforeEach
    void setUp() throws IOException {
        testFile = tempDir.resolve("test_family_members.json").toFile();
        repository = new FamilyMemberRepository(testFile);
    }
    
    @AfterEach
    void tearDown() {
        if (testFile.exists()) {
            testFile.delete();
        }
    }
    
    @Nested
    @DisplayName("Save")
    class Save {
        
        @Test
        @DisplayName("should save a new family member with generated ID")
        void shouldSaveNewFamilyMemberWithGeneratedId() {
            // Given
            FamilyMember member = new FamilyMember("John Smith");
            
            // When
            FamilyMember saved = repository.save(member);
            
            // Then
            assertEquals(1, saved.getId());
            assertEquals("John Smith", saved.getName());
            assertTrue(testFile.exists());
        }
        
        @Test
        @DisplayName("should save multiple family members with unique IDs")
        void shouldSaveMultipleFamilyMembersWithUniqueIds() {
            // Given
            FamilyMember member1 = new FamilyMember("John Smith");
            FamilyMember member2 = new FamilyMember("Jane Doe");
            
            // When
            FamilyMember saved1 = repository.save(member1);
            FamilyMember saved2 = repository.save(member2);
            
            // Then
            assertEquals(1, saved1.getId());
            assertEquals(2, saved2.getId());
            
            List<FamilyMember> all = repository.findAll();
            assertEquals(2, all.size());
        }
        
        @Test
        @DisplayName("should update existing family member")
        void shouldUpdateExistingFamilyMember() {
            // Given
            FamilyMember member = new FamilyMember("John Smith");
            FamilyMember saved = repository.save(member);
            saved.setName("John Doe");
            
            // When
            FamilyMember updated = repository.save(saved);
            
            // Then
            assertEquals(saved.getId(), updated.getId());
            assertEquals("John Doe", updated.getName());
            
            List<FamilyMember> all = repository.findAll();
            assertEquals(1, all.size());
            assertEquals("John Doe", all.get(0).getName());
        }
    }
    
    @Nested
    @DisplayName("Find By ID")
    class FindById {
        
        @Test
        @DisplayName("should find family member by ID when exists")
        void shouldFindFamilyMemberByIdWhenExists() {
            // Given
            FamilyMember member = new FamilyMember("John Smith");
            FamilyMember saved = repository.save(member);
            
            // When
            Optional<FamilyMember> found = repository.findById(saved.getId());
            
            // Then
            assertTrue(found.isPresent());
            assertEquals(saved.getId(), found.get().getId());
            assertEquals(saved.getName(), found.get().getName());
        }
        
        @Test
        @DisplayName("should return empty optional when ID does not exist")
        void shouldReturnEmptyOptionalWhenIdDoesNotExist() {
            // Given
            int nonExistentId = 999;
            
            // When
            Optional<FamilyMember> found = repository.findById(nonExistentId);
            
            // Then
            assertFalse(found.isPresent());
        }
        
        @Test
        @DisplayName("should find family member by string ID when exists")
        void shouldFindFamilyMemberByStringIdWhenExists() {
            // Given
            FamilyMember member = new FamilyMember("John Smith");
            FamilyMember saved = repository.save(member);
            String id = String.valueOf(saved.getId());
            
            // When
            Optional<FamilyMember> found = repository.findById(id);
            
            // Then
            assertTrue(found.isPresent());
            assertEquals(saved.getId(), found.get().getId());
            assertEquals(saved.getName(), found.get().getName());
        }
        
        @Test
        @DisplayName("should return empty optional for invalid string ID")
        void shouldReturnEmptyOptionalForInvalidStringId() {
            // Given
            String invalidId = "abc";
            
            // When
            Optional<FamilyMember> found = repository.findById(invalidId);
            
            // Then
            assertFalse(found.isPresent());
        }
    }
    
    @Nested
    @DisplayName("Find All")
    class FindAll {
        
        @Test
        @DisplayName("should find all family members")
        void shouldFindAllFamilyMembers() {
            // Given
            FamilyMember member1 = new FamilyMember("John Smith");
            FamilyMember member2 = new FamilyMember("Jane Doe");
            repository.save(member1);
            repository.save(member2);
            
            // When
            List<FamilyMember> all = repository.findAll();
            
            // Then
            assertEquals(2, all.size());
            assertTrue(all.stream().anyMatch(m -> m.getName().equals("John Smith")));
            assertTrue(all.stream().anyMatch(m -> m.getName().equals("Jane Doe")));
        }
        
        @Test
        @DisplayName("should return empty list when no family members exist")
        void shouldReturnEmptyListWhenNoFamilyMembersExist() {
            // Given
            // No family members saved
            
            // When
            List<FamilyMember> all = repository.findAll();
            
            // Then
            assertTrue(all.isEmpty());
        }
    }
    
    @Nested
    @DisplayName("Delete")
    class Delete {
        
        @Test
        @DisplayName("should delete family member by ID")
        void shouldDeleteFamilyMemberById() {
            // Given
            FamilyMember member = new FamilyMember("John Smith");
            FamilyMember saved = repository.save(member);
            
            // When
            repository.delete(saved.getId());
            
            // Then
            Optional<FamilyMember> found = repository.findById(saved.getId());
            assertFalse(found.isPresent());
        }
        
        @Test
        @DisplayName("should not throw exception when deleting non-existent ID")
        void shouldNotThrowExceptionWhenDeletingNonExistentId() {
            // Given
            int nonExistentId = 999;
            
            // When/Then
            assertDoesNotThrow(() -> repository.delete(nonExistentId));
        }
        
        @Test
        @DisplayName("should delete family member by string ID")
        void shouldDeleteFamilyMemberByStringId() {
            // Given
            FamilyMember member = new FamilyMember("John Smith");
            FamilyMember saved = repository.save(member);
            String id = String.valueOf(saved.getId());
            
            // When
            repository.delete(id);
            
            // Then
            Optional<FamilyMember> found = repository.findById(saved.getId());
            assertFalse(found.isPresent());
        }
        
        @Test
        @DisplayName("should not throw exception when deleting invalid string ID")
        void shouldNotThrowExceptionWhenDeletingInvalidStringId() {
            // Given
            String invalidId = "abc";
            
            // When/Then
            assertDoesNotThrow(() -> repository.delete(invalidId));
        }
    }
    
    @Nested
    @DisplayName("Update")
    class Update {
        
        @Test
        @DisplayName("should update family member")
        void shouldUpdateFamilyMember() {
            // Given
            FamilyMember member = new FamilyMember("John Smith");
            FamilyMember saved = repository.save(member);
            saved.setName("John Doe");
            
            // When
            FamilyMember updated = repository.update(saved);
            
            // Then
            assertEquals(saved.getId(), updated.getId());
            assertEquals("John Doe", updated.getName());
            
            Optional<FamilyMember> found = repository.findById(saved.getId());
            assertTrue(found.isPresent());
            assertEquals("John Doe", found.get().getName());
        }
        
        @Test
        @DisplayName("should throw exception when updating non-existent family member")
        void shouldThrowExceptionWhenUpdatingNonExistentFamilyMember() {
            // Given
            FamilyMember nonExistentMember = new FamilyMember(999, "Non Existent");
            
            // When/Then
            assertThrows(IllegalArgumentException.class, () -> repository.update(nonExistentMember));
        }
    }
    
    @Nested
    @DisplayName("File Persistence")
    class FilePersistence {
        
        @Test
        @DisplayName("should load family members from existing file")
        void shouldLoadFamilyMembersFromExistingFile() throws IOException {
            // Given
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            FamilyMember member1 = new FamilyMember(1, "John Smith");
            FamilyMember member2 = new FamilyMember(2, "Jane Doe");
            
            List<FamilyMember> members = Arrays.asList(member1, member2);
            mapper.writeValue(testFile, members);
            
            // When
            FamilyMemberRepository newRepository = new FamilyMemberRepository(testFile);
            List<FamilyMember> loadedMembers = newRepository.findAll();
            
            // Then
            assertEquals(2, loadedMembers.size());
            assertTrue(loadedMembers.stream().anyMatch(m -> m.getId() == 1 && m.getName().equals("John Smith")));
            assertTrue(loadedMembers.stream().anyMatch(m -> m.getId() == 2 && m.getName().equals("Jane Doe")));
        }
        
        @Test
        @DisplayName("should initialize with empty repository when file does not exist")
        void shouldInitializeWithEmptyRepositoryWhenFileDoesNotExist() {
            // Given
            File nonExistentFile = tempDir.resolve("non_existent.json").toFile();
            
            // When
            FamilyMemberRepository newRepository = new FamilyMemberRepository(nonExistentFile);
            
            // Then
            assertTrue(newRepository.findAll().isEmpty());
        }
        
        @Test
        @DisplayName("should save family members to file")
        void shouldSaveFamilyMembersToFile() throws IOException {
            // Given
            FamilyMember member1 = new FamilyMember("John Smith");
            FamilyMember member2 = new FamilyMember("Jane Doe");
            
            // When
            repository.save(member1);
            repository.save(member2);
            
            // Then
            assertTrue(testFile.exists());
            String content = new String(Files.readAllBytes(testFile.toPath()));
            assertTrue(content.contains("John Smith"));
            assertTrue(content.contains("Jane Doe"));
        }
    }
} 