package com.geobloc.prompt;

import android.util.Log;

import com.geobloc.handlers.AttributeTag;
import com.geobloc.shared.Utilities;
import com.geobloc.shared.Utilities.QuestionType;

public class MediaQuestionPrompt extends QuestionPrompt {
	private static final String TAG = "MediaQuestionPrompt";
	
	public MediaQuestionPrompt () {
		
		this.setType();
	}
	
	@Override
	public void setType() {
		type = QuestionType.GB_MEDIA;	
	}

	@Override
	public Object getAnswer() {
		return null;
	}

	@Override
	public void setAnswer(Object answer) {
		// TODO Auto-generated method stub
		
	}
}
