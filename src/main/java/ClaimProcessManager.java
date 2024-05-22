import java.util.List;

public interface ClaimProcessManager {
    void add(Claim claim);
    void update(Claim claim, ClaimStatus status);
    void delete(Claim claim);
    Claim getOne(String id);
    List<Claim> getAll(String customerId);
}