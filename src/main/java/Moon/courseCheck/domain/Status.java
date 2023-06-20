package Moon.courseCheck.domain;

public enum Status {
    DONE("done"),
    IN_PROGRESS("in progress"),
    HELP("help!"),
    IDLE("idle");

    private final String label;

    Status(String label){
        this.label = label;
    }

    public String getLabel(){
        return label;
    }
}
