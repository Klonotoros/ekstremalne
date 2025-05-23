package pl.edu.agh.isi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class FamilyMemberRepository {
    private final File file;
    private final ObjectMapper mapper;
    private Map<Integer, FamilyMember> familyMembers;
    private AtomicInteger nextId;

    public FamilyMemberRepository(File file) {
        this.file = file;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.familyMembers = new HashMap<>();
        this.nextId = new AtomicInteger(1);
        loadFamilyMembers();
    }

    private void loadFamilyMembers() {
        if (!file.exists()) {
            familyMembers = new HashMap<>();
            return;
        }
        try {
            FamilyMember[] loaded = mapper.readValue(file, FamilyMember[].class);
            familyMembers = new HashMap<>();
            for (FamilyMember member : loaded) {
                // Skip members with invalid data
                if (member.getId() <= 0 || member.getName() == null || member.getName().isEmpty()) {
                    continue;
                }
                
                familyMembers.put(member.getId(), member);
                if (member.getId() >= nextId.get()) {
                    nextId.set(member.getId() + 1);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading family members: " + e.getMessage());
            familyMembers = new HashMap<>();
        }
    }

    private void saveFamilyMembers() {
        try {
            if (!file.exists()) {
                // Create parent directories if needed
                File parentDir = file.getParentFile();
                if (parentDir != null) {
                    parentDir.mkdirs();
                }
                file.createNewFile();
            }
            mapper.writeValue(file, familyMembers.values());
        } catch (IOException e) {
            System.err.println("Error saving family members: " + e.getMessage());
        }
    }

    public FamilyMember save(FamilyMember member) {
        if (member.getId() == 0) {
            member.setId(nextId.getAndIncrement());
        }
        familyMembers.put(member.getId(), member);
        saveFamilyMembers();
        return member;
    }

    public Optional<FamilyMember> findById(int id) {
        return Optional.ofNullable(familyMembers.get(id));
    }

    public Optional<FamilyMember> findById(String id) {
        try {
            return findById(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public List<FamilyMember> findAll() {
        return new ArrayList<>(familyMembers.values());
    }

    public void delete(int id) {
        familyMembers.remove(id);
        saveFamilyMembers();
    }

    public void delete(String id) {
        try {
            delete(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            // Ignore invalid ID
        }
    }

    public FamilyMember update(FamilyMember member) {
        if (!familyMembers.containsKey(member.getId())) {
            throw new IllegalArgumentException("Family member not found with id: " + member.getId());
        }
        familyMembers.put(member.getId(), member);
        saveFamilyMembers();
        return member;
    }
} 