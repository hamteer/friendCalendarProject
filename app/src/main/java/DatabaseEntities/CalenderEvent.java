package DatabaseEntities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.api.client.util.DateTime;

import java.util.UUID;

@Entity(tableName = "CalenderEvent_table")
public class CalenderEvent {


    public String calenderID;
    @PrimaryKey
    @NonNull
    public String eventID;

    @NonNull
    public DateTime startTime;
    //String startTimeZone;
    @NonNull
    public DateTime endTime;
    //String endTimeZone;

    public String description;
    public String summary;
    public String location;
    public String creator;
    public DateTime updated;


    public CalenderEvent(String calenderID, String eventID, DateTime startTime, DateTime endTime, String description, String summary, String location,
                         String creator, DateTime updated){

        this.calenderID = calenderID;
        // schau ob eine calenderID uebergeben wurde falls ja nimm die, ansonsten erstell eine
        this.eventID = (eventID != null) ? eventID : String.valueOf(UUID.randomUUID());
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.summary = summary;
        this.location = location;
        this.creator = creator;
        this.updated = updated;
    }
    @Ignore
    @Override
    public String toString(){
        return calenderID + " | " + eventID + " | " + startTime + " | ";
    }

}
