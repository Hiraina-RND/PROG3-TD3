import java.time.Instant;
import java.util.Objects;

public class StockMovement {

    private int id;
    private StockValue value;
    private MovementTypeEnum type;
    private Instant creationDateTime;

    @Override
    public String toString() {
        return "StockMovement{" +
                "id=" + id +
                ", value=" + value +
                ", type=" + type +
                ", creationDateTime=" + creationDateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StockMovement that = (StockMovement) o;
        return id == that.id && Objects.equals(value, that.value) && type == that.type && Objects.equals(creationDateTime, that.creationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, type, creationDateTime);
    }

    public int getId() {
        return id;
    }

    public StockValue getValue() {
        return value;
    }

    public MovementTypeEnum getType() {
        return type;
    }

    public Instant getCreationDateTime() {
        return creationDateTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setValue(StockValue value) {
        this.value = value;
    }

    public void setType(MovementTypeEnum type) {
        this.type = type;
    }

    public void setCreationDateTime(Instant creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public StockMovement(int id, StockValue value, MovementTypeEnum type, Instant creationDateTime) {
        this.id = id;
        this.value = value;
        this.type = type;
        this.creationDateTime = creationDateTime;
    }
}
