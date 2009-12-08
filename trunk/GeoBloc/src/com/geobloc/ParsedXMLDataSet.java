package com.geobloc;

public class ParsedXMLDataSet {
	
	private String Form = null;
	private String Button = null;
	private String Name = null;
	private String MyString = null;
	private String Int = null;
	
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    public String getForm() {
         return Form;
    }
    public void setForm(String extractedString) {
         this.Form = extractedString;
    }
    public String getButton() {
         return Button;
    }
    public void setButton(String extractedString) {
         this.Button = extractedString;
    }
    public String getName(){
    	return Name;
    }
    public void setName(String extractedString){
    	this.Name = extractedString;
    }
    public String getMyString() {
        return MyString;
   }
    public void setMyString(String extractedString) {
        this.MyString = extractedString;
   }
    public String getInt() {
        return Int;
   }
    public void setInt(String extractedString) {
        this.Int = extractedString;
   }
    public String toString(){
         return "Todos los datos";
    } 

}
