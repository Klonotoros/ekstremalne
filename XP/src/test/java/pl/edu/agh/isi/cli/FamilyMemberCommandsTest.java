package pl.edu.agh.isi.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    
    @Nested
    @DisplayName("Add Family Member Command")
    class AddFamilyMemberCommandTest {
        
        @Test
        @DisplayName("should add family member and display success message")
        void shouldAddFamilyMemberAndDisplaySuccessMessage() throws Exception {
            // Given
            FamilyMemberService mockService = mock(FamilyMemberService.class);
            
            String name = "John Smith";
            FamilyMember member = new FamilyMember(1, name);
            
            when(mockService.createFamilyMember(name)).thenReturn(member);
            
            AddFamilyMemberCommand command = Mockito.spy(new AddFamilyMemberCommand());
            doReturn(mockService).when(command).createFamilyMemberService(any());
            command.name = name;
            command.familyMembersFile = tempDir.resolve("test_family_members.json").toFile();
            
            // When
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));
            
            try {
                command.call();
                
                // Then
                assertTrue(outContent.toString().contains("Family member added successfully with ID: 1"));
                verify(mockService).createFamilyMember(name);
            } finally {
                System.setOut(originalOut);
            }
        }
        
        @Test
        @DisplayName("should show examples when help is requested")
        void shouldShowExamplesWhenHelpIsRequested() throws Exception {
            // Given
            AddFamilyMemberCommand command = new AddFamilyMemberCommand();
            command.helpRequested = true;
            
            // When
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));
            
            try {
                command.call();
                
                // Then
                String output = outContent.toString();
                assertTrue(output.contains("Usage: add-member"));
                assertTrue(output.contains("Examples:"));
            } finally {
                System.setOut(originalOut);
            }
        }
    }
    
    @Nested
    @DisplayName("Remove Family Member Command")
    class RemoveFamilyMemberCommandTest {
        
        @Test
        @DisplayName("should remove family member when ID exists")
        void shouldRemoveFamilyMemberWhenIdExists() throws Exception {
            // Given
            FamilyMemberService mockService = mock(FamilyMemberService.class);
            
            String id = "1";
            FamilyMember member = new FamilyMember(1, "John Smith");
            
            when(mockService.getFamilyMember(id)).thenReturn(Optional.of(member));
            
            RemoveFamilyMemberCommand command = Mockito.spy(new RemoveFamilyMemberCommand());
            doReturn(mockService).when(command).createFamilyMemberService(any());
            command.id = id;
            command.familyMembersFile = tempDir.resolve("test_family_members.json").toFile();
            
            // When
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));
            
            try {
                command.call();
                
                // Then
                assertTrue(outContent.toString().contains("Family member with ID 1 was successfully removed"));
                verify(mockService).getFamilyMember(id);
                verify(mockService).deleteFamilyMember(id);
            } finally {
                System.setOut(originalOut);
            }
        }
        
        @Test
        @DisplayName("should show error when ID does not exist")
        void shouldShowErrorWhenIdDoesNotExist() throws Exception {
            // Given
            FamilyMemberService mockService = mock(FamilyMemberService.class);
            
            String id = "999";
            
            when(mockService.getFamilyMember(id)).thenReturn(Optional.empty());
            
            RemoveFamilyMemberCommand command = Mockito.spy(new RemoveFamilyMemberCommand());
            doReturn(mockService).when(command).createFamilyMemberService(any());
            command.id = id;
            command.familyMembersFile = tempDir.resolve("test_family_members.json").toFile();
            
            // When
            ByteArrayOutputStream errContent = new ByteArrayOutputStream();
            PrintStream originalErr = System.err;
            System.setErr(new PrintStream(errContent));
            
            try {
                command.call();
                
                // Then
                assertTrue(errContent.toString().contains("Error: Family member with ID 999 not found"));
                verify(mockService).getFamilyMember(id);
                verify(mockService, never()).deleteFamilyMember(anyString());
            } finally {
                System.setErr(originalErr);
            }
        }
    }
    
    @Nested
    @DisplayName("List Family Members Command")
    class ListFamilyMembersCommandTest {
        
        @Test
        @DisplayName("should list all family members")
        void shouldListAllFamilyMembers() throws Exception {
            // Given
            FamilyMemberService mockService = mock(FamilyMemberService.class);
            
            List<FamilyMember> members = new ArrayList<>();
            members.add(new FamilyMember(1, "John Smith"));
            members.add(new FamilyMember(2, "Jane Doe"));
            
            when(mockService.getAllFamilyMembers()).thenReturn(members);
            
            ListFamilyMembersCommand command = Mockito.spy(new ListFamilyMembersCommand());
            doReturn(mockService).when(command).createFamilyMemberService(any());
            command.familyMembersFile = tempDir.resolve("test_family_members.json").toFile();
            
            // When
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));
            
            try {
                command.call();
                
                // Then
                String output = outContent.toString();
                assertTrue(output.contains("Family Members:"));
                assertTrue(output.contains("John Smith"));
                assertTrue(output.contains("Jane Doe"));
                assertTrue(output.contains("Total: 2 member(s)"));
                verify(mockService).getAllFamilyMembers();
            } finally {
                System.setOut(originalOut);
            }
        }
        
        @Test
        @DisplayName("should show message when no family members exist")
        void shouldShowMessageWhenNoFamilyMembersExist() throws Exception {
            // Given
            FamilyMemberService mockService = mock(FamilyMemberService.class);
            
            List<FamilyMember> emptyList = new ArrayList<>();
            
            when(mockService.getAllFamilyMembers()).thenReturn(emptyList);
            
            ListFamilyMembersCommand command = Mockito.spy(new ListFamilyMembersCommand());
            doReturn(mockService).when(command).createFamilyMemberService(any());
            command.familyMembersFile = tempDir.resolve("test_family_members.json").toFile();
            
            // When
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));
            
            try {
                command.call();
                
                // Then
                assertTrue(outContent.toString().contains("No family members found"));
                verify(mockService).getAllFamilyMembers();
            } finally {
                System.setOut(originalOut);
            }
        }
    }
} 