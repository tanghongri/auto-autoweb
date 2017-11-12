package auto.web;

public enum BrowserEnum {
	BROWER_FIREFOX("Mozilla Firefox"), 
	B("text2"), 
	C("text3"), 
	D("text4");

	private final String text;
    private BrowserEnum(final String text){
        this.text=text;
    }
    @Override
    public String toString(){
        return text;
    }
}
