package musicDirectory.functions;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import musicDirectory.modal.Artist;

import static musicDirectory.constants.Constants.ARTISTS_TABLE;

public class UpdateArtistFunction {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());

    public APIGatewayProxyResponseEvent update(final APIGatewayProxyRequestEvent input, final Context context) throws JsonProcessingException {
        Artist payload = objectMapper.readValue(input.getBody(), Artist.class);

        Table table = dynamoDB.getTable(System.getenv(ARTISTS_TABLE));
        Item item = new Item().withPrimaryKey("id", input.getPathParameters().get("id"))
                .withString("name", payload.name)
                .withString("url", payload.url);
        table.putItem(item);

        String jsonOutput = objectMapper.writeValueAsString(payload);
        return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(jsonOutput);
    }
}
