package com.geobloc.prompt;

import com.geobloc.shared.Utilities.QuestionType;

public class LabelQuestionPrompt extends QuestionPrompt {
	
	public LabelQuestionPrompt (String name) {
		this.setQuestionName(name);
		this.setType();
	}
	
	@Override
	public void setType () {
		type = QuestionType.GB_LABEL;
	}

}
