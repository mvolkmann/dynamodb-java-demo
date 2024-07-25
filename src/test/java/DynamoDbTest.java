import com.example.model.Dog;
import com.example.service.DogService;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamoDbTest {

    @Test
    public void canGetTables() throws AssertionError {
        DynamoDbClient client = DynamoDbClient.builder()
                .region(Region.US_EAST_2)
                .endpointDiscoveryEnabled(true)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        ListTablesResponse tables = client.listTables();
        List<String> tableNames = tables.tableNames();
        assertEquals(1, tableNames.size());
        assertEquals("Dogs", tableNames.getFirst());
    }

    @Test
    public void canCRUD() throws AssertionError {
        DogService dogService = new DogService();

        dogService.deleteAll();
        String oscarId = dogService.add("Oscar", "German Shorthaired Pointer");
        String cometId = dogService.add("Comet", "Whippet");
        //dogService.printAll();

        List<Dog> list = dogService.getAllList();
        assertEquals(2, list.size());
        Dog comet = list.getFirst();
        assertEquals("Comet", comet.getName());
        assertEquals(cometId, comet.getId());
        Dog oscar = list.get(1);
        assertEquals("Oscar", oscar.getName());
        assertEquals(oscarId, oscar.getId());

        dogService.delete(oscarId);
        list = dogService.getAllList();
        assertEquals(1, list.size());

        dogService.rename(cometId, "Fireball");
        comet = dogService.get(cometId);
        assertEquals("Fireball", comet.getName());
    }
}
