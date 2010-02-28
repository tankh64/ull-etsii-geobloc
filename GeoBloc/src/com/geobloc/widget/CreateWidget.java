package com.geobloc.widget;

import android.content.Context;

import com.geobloc.prompt.LabelQuestionPrompt;
import com.geobloc.prompt.QuestionPrompt;

public class CreateWidget {
	
	static public QuestionWidget createWidget (QuestionPrompt qP, Context context) {
		
		QuestionWidget widget;
		
		switch (qP.getType()) {
		
		case GB_LABEL:
			widget = new LabelWidget(context);
			break;
		case GB_DATAINPUT:
			widget = new FieldWidget(context);
			break;
		case GB_BUTTON:
			widget = new ButtonWidget(context);
			break;
		default:
			widget = new LabelWidget(context);
			break;
		}
		
		if (widget != null) {
			widget.buildView(qP);
			return widget;
		}
		return null;
	}

}
