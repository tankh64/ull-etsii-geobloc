package com.geobloc.prompt;

public class ButtonQuestionPrompt extends QuestionPrompt {
	
	public ButtonQuestionPrompt (String name) {
		this.setQuestionName(name);
		this.setType();
	}
	
	@Override
	public void setType() {
		type = QuestionType.GB_BUTTON;	
	}
}
