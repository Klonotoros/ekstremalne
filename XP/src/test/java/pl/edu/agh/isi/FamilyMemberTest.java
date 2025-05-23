package pl.edu.agh.isi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Family Member")
class FamilyMemberTest {

    @Nested
    @DisplayName("Creation")
    class Creation {
        
        @Test
        @DisplayName("should create a family member with valid name")
        void shouldCreateFamilyMemberWithValidName() {
            // Given
            String name = "John Smith";
            
            // When
            FamilyMember member = new FamilyMember(name);
            
            // Then
            assertEquals(name, member.getName());
            assertEquals(0, member.getId());
            assertNotNull(member.getCreatedAt());
        }
        
        @Test
        @DisplayName("should create a family member with valid ID and name")
        void shouldCreateFamilyMemberWithValidIdAndName() {
            // Given
            int id = 1;
            String name = "John Smith";
            
            // When
            FamilyMember member = new FamilyMember(id, name);
            
            // Then
            assertEquals(id, member.getId());
            assertEquals(name, member.getName());
            assertNotNull(member.getCreatedAt());
        }
        
        @Test
        @DisplayName("should throw exception when creating with null name")
        void shouldThrowExceptionWhenCreatingWithNullName() {
            // Given/When/Then
            assertThrows(IllegalArgumentException.class, () -> new FamilyMember(null));
        }
        
        @Test
        @DisplayName("should throw exception when creating with empty name")
        void shouldThrowExceptionWhenCreatingWithEmptyName() {
            // Given/When/Then
            assertThrows(IllegalArgumentException.class, () -> new FamilyMember(""));
            assertThrows(IllegalArgumentException.class, () -> new FamilyMember("   "));
        }
    }
    
    @Nested
    @DisplayName("Setters and Getters")
    class SettersAndGetters {
        
        @Test
        @DisplayName("should set and get ID correctly")
        void shouldSetAndGetIdCorrectly() {
            // Given
            FamilyMember member = new FamilyMember("John Smith");
            int id = 5;
            
            // When
            member.setId(id);
            
            // Then
            assertEquals(id, member.getId());
        }
        
        @Test
        @DisplayName("should set and get name correctly")
        void shouldSetAndGetNameCorrectly() {
            // Given
            FamilyMember member = new FamilyMember("John Smith");
            String newName = "John Doe";
            
            // When
            member.setName(newName);
            
            // Then
            assertEquals(newName, member.getName());
        }
        
        @Test
        @DisplayName("should throw exception when setting null name")
        void shouldThrowExceptionWhenSettingNullName() {
            // Given
            FamilyMember member = new FamilyMember("John Smith");
            
            // When/Then
            assertThrows(IllegalArgumentException.class, () -> member.setName(null));
        }
        
        @Test
        @DisplayName("should throw exception when setting empty name")
        void shouldThrowExceptionWhenSettingEmptyName() {
            // Given
            FamilyMember member = new FamilyMember("John Smith");
            
            // When/Then
            assertThrows(IllegalArgumentException.class, () -> member.setName(""));
            assertThrows(IllegalArgumentException.class, () -> member.setName("   "));
        }
        
        @Test
        @DisplayName("should set and get created at correctly")
        void shouldSetAndGetCreatedAtCorrectly() {
            // Given
            FamilyMember member = new FamilyMember("John Smith");
            LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 12, 0);
            
            // When
            member.setCreatedAt(createdAt);
            
            // Then
            assertEquals(createdAt, member.getCreatedAt());
        }
    }
    
    @Nested
    @DisplayName("Equals and HashCode")
    class EqualsAndHashCode {
        
        @Test
        @DisplayName("should be equal if IDs are the same")
        void shouldBeEqualIfIdsAreTheSame() {
            // Given
            FamilyMember member1 = new FamilyMember(1, "John Smith");
            FamilyMember member2 = new FamilyMember(1, "Different Name");
            
            // When/Then
            assertEquals(member1, member2);
            assertEquals(member1.hashCode(), member2.hashCode());
        }
        
        @Test
        @DisplayName("should not be equal if IDs are different")
        void shouldNotBeEqualIfIdsAreDifferent() {
            // Given
            FamilyMember member1 = new FamilyMember(1, "John Smith");
            FamilyMember member2 = new FamilyMember(2, "John Smith");
            
            // When/Then
            assertNotEquals(member1, member2);
            assertNotEquals(member1.hashCode(), member2.hashCode());
        }
    }
    
    @Test
    @DisplayName("should have a meaningful toString representation")
    void shouldHaveMeaningfulToString() {
        // Given
        FamilyMember member = new FamilyMember(1, "John Smith");
        
        // When
        String toString = member.toString();
        
        // Then
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name='John Smith'"));
        assertTrue(toString.contains("createdAt="));
    }
} 