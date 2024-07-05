import model.Dog;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;

import java.util.Iterator;

public class Demo {

    private final DynamoDbTable<Dog> dogsTable;

    private Demo() {
        DynamoDbClient client = DynamoDbClient.builder()
                .region(Region.US_EAST_2)
                .endpointDiscoveryEnabled(true)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        ListTablesResponse tables = client.listTables();
        System.out.println(">>> tables = " + tables);

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build();
        dogsTable =
                enhancedClient.table("Dogs", TableSchema.fromBean(Dog.class));

        // addDog("Maisey", "Treeing Walker Coonhound");
        // deleteDog(4);
        // System.out.println(getDog(1));
        // renameDog(1, "Fireball");
        printDogs();
    }

    public static void main(String[] args) {
        new Demo();
    }

    void addDog(String name, String breed) {
        Dog dog = new Dog();
        dog.setName(name);
        dog.setBreed(breed);
        try {
            dogsTable.putItem(dog);
        } catch (DynamoDbException e) {
            System.err.println("addDog: " + e.getMessage());
            System.exit(1);
        }
    }

    void deleteDog(int id) {
        try {
            Key key = Key.builder().partitionValue(id).build();
            dogsTable.deleteItem(key);
        } catch (DynamoDbException e) {
            System.err.println("deleteDog: " + e.getMessage());
            System.exit(1);
        }
    }

    Dog getDog(int id) {
        Key key = Key.builder().partitionValue(id).build();
        try {
            return dogsTable.getItem(key);
        } catch (DynamoDbException e) {
            System.err.println("getDog: " + e.getMessage());
            System.exit(1);
            return null;
        }
    }

    void renameDog(int id, String newName) {
        Dog dog = getDog(id);
        dog.setName(newName);
        try {
            dogsTable.putItem(dog);
        } catch (DynamoDbException e) {
            System.err.println("addDog: " + e.getMessage());
            System.exit(1);
        }
    }

    void printDogs() {
        Iterator<Dog> results = dogsTable.scan().items().iterator();
        while (results.hasNext()) {
            Dog dog = results.next();
            System.out.println(">>> " + dog);
        }
    }
}