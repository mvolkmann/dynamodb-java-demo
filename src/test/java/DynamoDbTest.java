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
        assertEquals("Dogs", tableNames.get(0));
    }

    @Test
    public void canCRUD() throws AssertionError {
        DogService dogService = new DogService();

        dogService.deleteAll();
        dogService.add("Oscar", "German Shorthaired Pointer");
        dogService.add("Comet", "Whippet");
        //dogService.printAll();

        List<Dog> list = dogService.getAllList();
        assertEquals(2, list.size());
        Dog oscar = list.get(0);
        assertEquals("Oscar", oscar.getName());
        Dog comet = list.get(1);
        assertEquals("Comet", comet.getName());

        dogService.delete(oscar.getId());
        list = dogService.getAllList();
        assertEquals(1, list.size());

        dogService.rename(comet.getId(), "Fireball");
        comet = dogService.get(comet.getId());
        assertEquals("Fireball", comet.getName());
    }
}
