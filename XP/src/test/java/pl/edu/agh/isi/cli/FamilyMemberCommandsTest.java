package pl.edu.agh.isi.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.edu.agh.isi.FamilyMember;
import pl.edu.agh.isi.FamilyMemberRepository;
import pl.edu.agh.isi.FamilyMemberService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Family Member CLI Commands")
class FamilyMemberCommandsTest {

    @TempDir
    Path tempDir;
    
    @Mock
    FamilyMemberRepository repository;
    
    @Mock
    FamilyMemberService service;
    
    @Nested
    @DisplayName("Add Family Member Command")
    class AddFamilyMemberCommandTest {
        
        @Test
        @DisplayName("should add family member and display success message")
        void shouldAddFamilyMemberAndDisplaySuccessMessage() {
            // Given
            String name = "John Smith";
            FamilyMember member = new FamilyMember(1, name);
            
            // Create a custom AddFamilyMemberCommand that uses our mocked service
            AddFamilyMemberCommand command = new AddFamilyMemberCommand() {
                @Override
                public Integer call() throws Exception {
                    FamilyMemberRepository repository = mock(FamilyMemberRepository.class);
                    FamilyMemberService service = mock(FamilyMemberService.class);
                    
                    when(service.createFamilyMember(name)).thenReturn(member);
                    
                    // Capture standard output
                    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                    PrintStream originalOut = System.out;
                    System.setOut(new PrintStream(outContent));
                    
                    try {
                        this.name = name;
                        this.familyMembersFile = tempDir.resolve("test_family_members.json").toFile();
                        
                        // Replace actual repository creation with our mock
                        return super.call();
                    } finally {
                        System.setOut(originalOut);
                    }
                }
            };
            
            // When
            try {
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(new PrintStream(outContent));
                
                command.call();
                
                System.setOut(originalOut);
                
                // Then
                assertTrue(outContent.toString().contains("Family member added successfully with ID: 1"));
            } catch (Exception e) {
                fail("Exception should not be thrown: " + e.getMessage());
            }
        }
        
        @Test
        @DisplayName("should show examples when help is requested")
        void shouldShowExamplesWhenHelpIsRequested() {
            // Given
            AddFamilyMemberCommand command = new AddFamilyMemberCommand();
            command.helpRequested = true;
            
            // When
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));
            
            try {
                command.call();
            } catch (Exception e) {
                fail("Exception should not be thrown: " + e.getMessage());
            } finally {
                System.setOut(originalOut);
            }
            
            // Then
            String output = outContent.toString();
            assertTrue(output.contains("Usage: add-member"));
            assertTrue(output.contains("Examples:"));
        }
    }
    
    @Nested
    @DisplayName("Remove Family Member Command")
    class RemoveFamilyMemberCommandTest {
        
        @Test
        @DisplayName("should remove family member when ID exists")
        void shouldRemoveFamilyMemberWhenIdExists() {
            // Given
            String id = "1";
            FamilyMember member = new FamilyMember(1, "John Smith");
            
            // Create a custom RemoveFamilyMemberCommand that uses our mocked service
            RemoveFamilyMemberCommand command = new RemoveFamilyMemberCommand() {
                @Override
                public Integer call() throws Exception {
                    FamilyMemberRepository repository = mock(FamilyMemberRepository.class);
                    FamilyMemberService service = mock(FamilyMemberService.class);
                    
                    when(service.getFamilyMember(id)).thenReturn(Optional.of(member));
                    
                    // Capture standard output
                    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                    PrintStream originalOut = System.out;
                    System.setOut(new PrintStream(outContent));
                    
                    try {
                        this.id = id;
                        this.familyMembersFile = tempDir.resolve("test_family_members.json").toFile();
                        
                        // Replace actual repository creation with our mock
                        return super.call();
                    } finally {
                        System.setOut(originalOut);
                    }
                }
            };
            
            // When
            try {
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(new PrintStream(outContent));
                
                command.call();
                
                System.setOut(originalOut);
                
                // Then
                assertTrue(outContent.toString().contains("Family member with ID 1 was successfully removed"));
            } catch (Exception e) {
                fail("Exception should not be thrown: " + e.getMessage());
            }
        }
        
        @Test
        @DisplayName("should show error when ID does not exist")
        void shouldShowErrorWhenIdDoesNotExist() {
            // Given
            String id = "999";
            
            // Create a custom RemoveFamilyMemberCommand that uses our mocked service
            RemoveFamilyMemberCommand command = new RemoveFamilyMemberCommand() {
                @Override
                public Integer call() throws Exception {
                    FamilyMemberRepository repository = mock(FamilyMemberRepository.class);
                    FamilyMemberService service = mock(FamilyMemberService.class);
                    
                    when(service.getFamilyMember(id)).thenReturn(Optional.empty());
                    
                    // Capture standard error
                    ByteArrayOutputStream errContent = new ByteArrayOutputStream();
                    PrintStream originalErr = System.err;
                    System.setErr(new PrintStream(errContent));
                    
                    try {
                        this.id = id;
                        this.familyMembersFile = tempDir.resolve("test_family_members.json").toFile();
                        
                        // Replace actual repository creation with our mock
                        return super.call();
                    } finally {
                        System.setErr(originalErr);
                    }
                }
            };
            
            // When
            try {
                ByteArrayOutputStream errContent = new ByteArrayOutputStream();
                PrintStream originalErr = System.err;
                System.setErr(new PrintStream(errContent));
                
                command.call();
                
                System.setErr(originalErr);
                
                // Then
                assertTrue(errContent.toString().contains("Error: Family member with ID 999 not found"));
            } catch (Exception e) {
                fail("Exception should not be thrown: " + e.getMessage());
            }
        }
    }
    
    @Nested
    @DisplayName("List Family Members Command")
    class ListFamilyMembersCommandTest {
        
        @Test
        @DisplayName("should list all family members")
        void shouldListAllFamilyMembers() {
            // Given
            List<FamilyMember> members = new ArrayList<>();
            members.add(new FamilyMember(1, "John Smith"));
            members.add(new FamilyMember(2, "Jane Doe"));
            
            // Create a custom ListFamilyMembersCommand that uses our mocked service
            ListFamilyMembersCommand command = new ListFamilyMembersCommand() {
                @Override
                public Integer call() throws Exception {
                    FamilyMemberRepository repository = mock(FamilyMemberRepository.class);
                    FamilyMemberService service = mock(FamilyMemberService.class);
                    
                    when(service.getAllFamilyMembers()).thenReturn(members);
                    
                    // Capture standard output
                    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                    PrintStream originalOut = System.out;
                    System.setOut(new PrintStream(outContent));
                    
                    try {
                        this.familyMembersFile = tempDir.resolve("test_family_members.json").toFile();
                        
                        // Replace actual repository creation with our mock
                        return super.call();
                    } finally {
                        System.setOut(originalOut);
                    }
                }
            };
            
            // When
            try {
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(new PrintStream(outContent));
                
                command.call();
                
                System.setOut(originalOut);
                
                // Then
                String output = outContent.toString();
                assertTrue(output.contains("Family Members:"));
                assertTrue(output.contains("John Smith"));
                assertTrue(output.contains("Jane Doe"));
                assertTrue(output.contains("Total: 2 member(s)"));
            } catch (Exception e) {
                fail("Exception should not be thrown: " + e.getMessage());
            }
        }
        
        @Test
        @DisplayName("should show message when no family members exist")
        void shouldShowMessageWhenNoFamilyMembersExist() {
            // Given
            List<FamilyMember> emptyList = new ArrayList<>();
            
            // Create a custom ListFamilyMembersCommand that uses our mocked service
            ListFamilyMembersCommand command = new ListFamilyMembersCommand() {
                @Override
                public Integer call() throws Exception {
                    FamilyMemberRepository repository = mock(FamilyMemberRepository.class);
                    FamilyMemberService service = mock(FamilyMemberService.class);
                    
                    when(service.getAllFamilyMembers()).thenReturn(emptyList);
                    
                    // Capture standard output
                    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                    PrintStream originalOut = System.out;
                    System.setOut(new PrintStream(outContent));
                    
                    try {
                        this.familyMembersFile = tempDir.resolve("test_family_members.json").toFile();
                        
                        // Replace actual repository creation with our mock
                        return super.call();
                    } finally {
                        System.setOut(originalOut);
                    }
                }
            };
            
            // When
            try {
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(new PrintStream(outContent));
                
                command.call();
                
                System.setOut(originalOut);
                
                // Then
                assertTrue(outContent.toString().contains("No family members found"));
            } catch (Exception e) {
                fail("Exception should not be thrown: " + e.getMessage());
            }
        }
    }
} 