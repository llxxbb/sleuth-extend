package sleuth.extend;

public class ClassForTest {

    public String publicString;

    private String privateString;

    public String getPrivateString() {
        return this.privateString;
    }

    public void setPrivateString(String value) {
        this.privateString = value;
    }

    public ClassB b = new ClassB();
}
