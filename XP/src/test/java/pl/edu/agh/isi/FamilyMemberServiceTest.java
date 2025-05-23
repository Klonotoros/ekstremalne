package pl.edu.agh.isi;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Family Member Service")
class FamilyMemberServiceTest {

    @Mock
    private FamilyMemberRepository repository;

    private FamilyMemberService service;

    @BeforeEach
    void setUp() {
        service = new FamilyMemberService(repository);
    }

    @Nested
    @DisplayName("Create Family Member")
    class CreateFamilyMember {

        @Test
        @DisplayName("should create a family member with valid name")
        void shouldCreateFamilyMemberWithValidName() {
            // Given
            String name = "John Smith";
            FamilyMember member = new FamilyMember(name);
            FamilyMember savedMember = new FamilyMember(1, name);
            
            when(repository.save(any(FamilyMember.class))).thenReturn(savedMember);

            // When
            FamilyMember result = service.createFamilyMember(name);

            // Then
            verify(repository).save(any(FamilyMember.class));
            assertEquals(savedMember, result);
            assertEquals(1, result.getId());
            assertEquals(name, result.getName());
        }

        @Test
        @DisplayName("should throw exception when creating with null name")
        void shouldThrowExceptionWhenCreatingWithNullName() {
            // Given/When/Then
            assertThrows(IllegalArgumentException.class, () -> service.createFamilyMember(null));
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("should throw exception when creating with empty name")
        void shouldThrowExceptionWhenCreatingWithEmptyName() {
            // Given/When/Then
            assertThrows(IllegalArgumentException.class, () -> service.createFamilyMember(""));
            assertThrows(IllegalArgumentException.class, () -> service.createFamilyMember("   "));
            verifyNoInteractions(repository);
        }
    }

    @Nested
    @DisplayName("Get Family Member")
    class GetFamilyMember {

        @Test
        @DisplayName("should return family member when ID exists")
        void shouldReturnFamilyMemberWhenIdExists() {
            // Given
            int id = 1;
            FamilyMember member = new FamilyMember(id, "John Smith");
            when(repository.findById(id)).thenReturn(Optional.of(member));

            // When
            Optional<FamilyMember> result = service.getFamilyMember(id);

            // Then
            assertTrue(result.isPresent());
            assertEquals(member, result.get());
        }

        @Test
        @DisplayName("should return empty optional when ID does not exist")
        void shouldReturnEmptyOptionalWhenIdDoesNotExist() {
            // Given
            int id = 999;
            when(repository.findById(id)).thenReturn(Optional.empty());

            // When
            Optional<FamilyMember> result = service.getFamilyMember(id);

            // Then
            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("should handle string ID conversion")
        void shouldHandleStringIdConversion() {
            // Given
            String id = "1";
            FamilyMember member = new FamilyMember(1, "John Smith");
            when(repository.findById(id)).thenReturn(Optional.of(member));

            // When
            Optional<FamilyMember> result = service.getFamilyMember(id);

            // Then
            assertTrue(result.isPresent());
            assertEquals(member, result.get());
        }
    }

    @Nested
    @DisplayName("Get All Family Members")
    class GetAllFamilyMembers {

        @Test
        @DisplayName("should return all family members")
        void shouldReturnAllFamilyMembers() {
            // Given
            List<FamilyMember> members = new ArrayList<>();
            members.add(new FamilyMember(1, "John Smith"));
            members.add(new FamilyMember(2, "Jane Doe"));
            
            when(repository.findAll()).thenReturn(members);

            // When
            List<FamilyMember> result = service.getAllFamilyMembers();

            // Then
            assertEquals(2, result.size());
            assertEquals(members, result);
        }

        @Test
        @DisplayName("should return empty list when no family members exist")
        void shouldReturnEmptyListWhenNoFamilyMembersExist() {
            // Given
            when(repository.findAll()).thenReturn(new ArrayList<>());

            // When
            List<FamilyMember> result = service.getAllFamilyMembers();

            // Then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Delete Family Member")
    class DeleteFamilyMember {

        @Test
        @DisplayName("should delete family member by ID")
        void shouldDeleteFamilyMemberById() {
            // Given
            int id = 1;

            // When
            service.deleteFamilyMember(id);

            // Then
            verify(repository).delete(id);
        }

        @Test
        @DisplayName("should handle string ID deletion")
        void shouldHandleStringIdDeletion() {
            // Given
            String id = "1";

            // When
            service.deleteFamilyMember(id);

            // Then
            verify(repository).delete(id);
        }
    }

    @Nested
    @DisplayName("Update Family Member")
    class UpdateFamilyMember {

        @Test
        @DisplayName("should update family member with valid name")
        void shouldUpdateFamilyMemberWithValidName() {
            // Given
            int id = 1;
            String oldName = "John Smith";
            String newName = "John Doe";
            
            FamilyMember existingMember = new FamilyMember(id, oldName);
            FamilyMember updatedMember = new FamilyMember(id, newName);
            
            when(repository.findById(id)).thenReturn(Optional.of(existingMember));
            when(repository.update(any(FamilyMember.class))).thenReturn(updatedMember);

            // When
            FamilyMember result = service.updateFamilyMember(id, newName);

            // Then
            verify(repository).findById(id);
            verify(repository).update(any(FamilyMember.class));
            assertEquals(updatedMember, result);
            assertEquals(newName, result.getName());
        }

        @Test
        @DisplayName("should throw exception when updating with non-existent ID")
        void shouldThrowExceptionWhenUpdatingWithNonExistentId() {
            // Given
            int id = 999;
            String newName = "John Doe";
            
            when(repository.findById(id)).thenReturn(Optional.empty());

            // When/Then
            assertThrows(IllegalArgumentException.class, () -> service.updateFamilyMember(id, newName));
            verify(repository).findById(id);
            verify(repository, never()).update(any(FamilyMember.class));
        }

        @Test
        @DisplayName("should handle string ID update")
        void shouldHandleStringIdUpdate() {
            // Given
            String id = "1";
            int intId = 1;
            String oldName = "John Smith";
            String newName = "John Doe";
            
            FamilyMember existingMember = new FamilyMember(intId, oldName);
            FamilyMember updatedMember = new FamilyMember(intId, newName);
            
            when(repository.findById(intId)).thenReturn(Optional.of(existingMember));
            when(repository.update(any(FamilyMember.class))).thenReturn(updatedMember);

            // When
            FamilyMember result = service.updateFamilyMember(id, newName);

            // Then
            assertEquals(updatedMember, result);
            assertEquals(newName, result.getName());
        }

        @Test
        @DisplayName("should throw exception when updating with invalid string ID")
        void shouldThrowExceptionWhenUpdatingWithInvalidStringId() {
            // Given
            String id = "invalid";

            // When/Then
            assertThrows(IllegalArgumentException.class, () -> service.updateFamilyMember(id, "New Name"));
            verifyNoInteractions(repository);
        }
    }
} 