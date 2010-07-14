package com.geobloc.widget;

import android.content.Context;
import android.view.ViewGroup;

import com.geobloc.prompt.LabelQuestionPrompt;
import com.geobloc.prompt.QuestionPrompt;

public class CreateWidget {
	
	static public IQuestionWidget createWidget (QuestionPrompt qP, Context context, ViewGroup parent) {
		
		IQuestionWidget widget;
		
		switch (qP.getType()) {
		
		case GB_LABEL:
			widget = new LabelWidget(context);
			break;
		case GB_DATAINPUT:
			widget = new FieldWidget(context);
			break;
		case GB_CHECKBOX:
			widget = new CheckboxWidget(context);
			break;
		case GB_CHECKBOX_THREE:
			widget = new CheckboxThreeWidget(context);
			break;
		case GB_SINGLE_LIST:
			widget = new SingleListWidget(context);
			break;
		case GB_MULTIPLE_LIST:
			widget = new MultipleListWidget(context);
			break;
		//case GB_MEDIA:
			/* Needs the parent View for inflate */
			//widget = new MediaWidget (context, parent);
			//((MediaWidget)widget).buildViewParent(parent);
			//break;
		default:
			widget = null;//new LabelWidget(context);
			break;
		}
		
		if (widget != null) {
			widget.buildView(qP);
			return widget;
		}
		return null;
	}

}
