package musicDirectory.functions;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.HashMap;
import java.util.Map;

import static musicDirectory.constants.Constants.ARTISTS_TABLE;

public class DeleteArtistFunction {

    public APIGatewayProxyResponseEvent delete(final APIGatewayProxyRequestEvent input, final Context context) {
        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();

        Map<String, AttributeValue> map = new HashMap<>();
        map.put("id", new AttributeValue(input.getPathParameters().get("id")));
        dynamoDB.deleteItem(System.getenv(ARTISTS_TABLE ), map);

        return new APIGatewayProxyResponseEvent().withStatusCode(200);
    }
}
