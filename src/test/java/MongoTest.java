import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class MongoTest {
    @Test
    public void insertTest(){
        BasicDBList dbList = new BasicDBList();
        for(int i=0;i<10000;i++){
            DBObject dbo = new BasicDBObject("_id",UUID.randomUUID().toString().replace("-",""));
            ((BasicDBObject) dbo).append("num",i);
        }
    }
}
