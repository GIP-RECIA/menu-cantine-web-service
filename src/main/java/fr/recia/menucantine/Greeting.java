package fr.recia.menucantine;

public class Greeting {

   
    private  String content;

    public void setContent(String content) {
		this.content = content;
	}

    public Greeting(){}

	public Greeting( String content) {
       super();
        this.content = content;
    }

 

    public String getContent() {
        return content;
    }
}
