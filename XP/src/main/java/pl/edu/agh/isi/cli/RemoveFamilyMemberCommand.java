package pl.edu.agh.isi.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

import pl.edu.agh.isi.FamilyMemberRepository;
import pl.edu.agh.isi.FamilyMemberService;

@Command(
    name = "remove-member",
    description = "Remove a family member",
    mixinStandardHelpOptions = false
)
public class RemoveFamilyMemberCommand implements Callable<Integer> {
    
    @Parameters(index = "0", description = "Family member ID", arity = "1")
    private String id;
    
    @Option(names = {"-f", "--file"}, description = "Family members data file", defaultValue = "family_members.json", hidden = true)
    private File familyMembersFile;
    
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Show help message")
    private boolean helpRequested = false;

    @Override
    public Integer call() {
        try {
            if (helpRequested) {
                showExamples();
                return 0;
            }
            
            FamilyMemberRepository repository = new FamilyMemberRepository(familyMembersFile);
            FamilyMemberService service = new FamilyMemberService(repository);
            
            if (service.getFamilyMember(id).isPresent()) {
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
        System.out.println("  remove-member 1      - Remove family member with ID 1");
        System.out.println("  remove-member 2      - Remove family member with ID 2");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -h, --help           Show this help message");
    }
} 