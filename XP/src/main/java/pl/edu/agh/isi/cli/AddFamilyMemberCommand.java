package pl.edu.agh.isi.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

import pl.edu.agh.isi.FamilyMember;
import pl.edu.agh.isi.FamilyMemberRepository;
import pl.edu.agh.isi.FamilyMemberService;

@Command(
    name = "add-member",
    description = "Add a new family member",
    mixinStandardHelpOptions = false
)
public class AddFamilyMemberCommand implements Callable<Integer> {
    
    @Parameters(index = "0", description = "Family member name", arity = "1")
    private String name;
    
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
            
            FamilyMember member = service.createFamilyMember(name);
            System.out.println("Family member added successfully with ID: " + member.getId());
            return 0;
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
        System.out.println("Usage: add-member \"Family Member Name\"");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  add-member \"John Smith\"        - Add John Smith as a family member");
        System.out.println("  add-member \"Anna Kowalska\"     - Add Anna Kowalska as a family member");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -h, --help                   Show this help message");
    }
} 