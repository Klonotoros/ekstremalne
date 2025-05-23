package pl.edu.agh.isi.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Callable;

import pl.edu.agh.isi.FamilyMember;
import pl.edu.agh.isi.FamilyMemberRepository;
import pl.edu.agh.isi.FamilyMemberService;

@Command(
    name = "list-members",
    description = "List all family members",
    mixinStandardHelpOptions = false
)
public class ListFamilyMembersCommand implements Callable<Integer> {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
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
            
            List<FamilyMember> members = service.getAllFamilyMembers();
            
            if (members.isEmpty()) {
                System.out.println("No family members found");
                return 0;
            }
            
            System.out.println("Family Members:");
            System.out.println("-----------------------------------------------");
            System.out.println("ID | Name                    | Created At");
            System.out.println("-----------------------------------------------");
            
            for (FamilyMember member : members) {
                System.out.printf("%-2d | %-24s | %s%n", 
                        member.getId(), 
                        member.getName(), 
                        member.getCreatedAt().format(DATE_FORMATTER));
            }
            
            System.out.println("-----------------------------------------------");
            System.out.println("Total: " + members.size() + " member(s)");
            
            return 0;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return 2;
        }
    }
    
    // Protected method for better testability
    protected FamilyMemberService createFamilyMemberService(File file) {
        FamilyMemberRepository repository = new FamilyMemberRepository(file);
        return new FamilyMemberService(repository);
    }
    
    private void showExamples() {
        System.out.println("Usage: list-members");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  list-members       - List all family members");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -h, --help         Show this help message");
    }
} 