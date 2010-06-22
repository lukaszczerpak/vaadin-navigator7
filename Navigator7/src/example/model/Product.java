package example.model;

public class Product extends BaseEntity {
    long partNumber;  // Different from id :-)
    String label;
    
    public long getPartNumber() {
        return partNumber;
    }
    public void setPartNumber(long partNumber) {
        this.partNumber = partNumber;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    
    
}
