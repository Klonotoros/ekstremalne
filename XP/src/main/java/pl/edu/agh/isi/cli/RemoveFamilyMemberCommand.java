package pl.edu.agh.isi.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.Optional;

import pl.edu.agh.isi.FamilyMember;
import pl.edu.agh.isi.FamilyMemberRepository;
import pl.edu.agh.isi.FamilyMemberService;

@Command(
    name = "remove-member",
    description = "Remove a family member",
    mixinStandardHelpOptions = false
)
public class RemoveFamilyMemberCommand implements Callable<Integer> {
    
    @Parameters(index = "0", description = "Family member ID", arity = "1")
    protected String id;
    
    @Option(names = {"-f", "--file"}, description = "Family members data file", defaultValue = "family_members.json", hidden = true)
    protected File familyMembersFile;
    
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Show help message")
    protected boolean helpRequested = false;

    @Override
    public Integer call() throws Exception {
        try {
            if (helpRequested) {
                showExamples();
                return 0;
            }
            
            FamilyMemberService service = createFamilyMemberService(familyMembersFile);
            
            Optional<FamilyMember> memberOpt = service.getFamilyMember(id);
            if (memberOpt.isPresent()) {
                service.deleteFamilyMember(id);
                System.out.println("Family member with ID " + id + " was successfully removed");
                return 0;
            } else {
                System.err.println("Error: Family member with ID " + id + " not found");
                return 1;
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return 2;
        }
    }
    
    private void showExamples() {
        System.out.println("Usage: remove-member ID");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  remove-member 1       - Remove family member with ID 1");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -h, --help            Show this help message");
    }
    
    // Protected method for better testability
    protected FamilyMemberService createFamilyMemberService(File file) {
        FamilyMemberRepository repository = new FamilyMemberRepository(file);
        return new FamilyMemberService(repository);
    }
} 