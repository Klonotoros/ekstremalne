package pl.edu.agh.isi;

import java.util.List;
import java.util.Optional;

public class FamilyMemberService {
    private final FamilyMemberRepository familyMemberRepository;

    public FamilyMemberService(FamilyMemberRepository familyMemberRepository) {
        this.familyMemberRepository = familyMemberRepository;
    }

    public FamilyMember createFamilyMember(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        FamilyMember member = new FamilyMember(name);
        return familyMemberRepository.save(member);
    }

    public Optional<FamilyMember> getFamilyMember(int id) {
        return familyMemberRepository.findById(id);
    }

    public Optional<FamilyMember> getFamilyMember(String id) {
        return familyMemberRepository.findById(id);
    }

    public List<FamilyMember> getAllFamilyMembers() {
        return familyMemberRepository.findAll();
    }

    public void deleteFamilyMember(int id) {
        familyMemberRepository.delete(id);
    }

    public void deleteFamilyMember(String id) {
        familyMemberRepository.delete(id);
    }

    public FamilyMember updateFamilyMember(int id, String name) {
        FamilyMember member = familyMemberRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Family member not found with id: " + id));
        
        if (name != null && !name.trim().isEmpty()) {
            member.setName(name);
        }
        
        return familyMemberRepository.update(member);
    }

    public FamilyMember updateFamilyMember(String id, String name) {
        try {
            return updateFamilyMember(Integer.parseInt(id), name);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid family member ID format: " + id);
        }
    }
} 