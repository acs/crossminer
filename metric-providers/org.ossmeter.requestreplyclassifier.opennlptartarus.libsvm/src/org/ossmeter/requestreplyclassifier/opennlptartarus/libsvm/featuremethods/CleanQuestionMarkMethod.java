package org.ossmeter.requestreplyclassifier.opennlptartarus.libsvm.featuremethods;

import org.ossmeter.requestreplyclassifier.opennlptartarus.libsvm.ClassificationInstance;

public class CleanQuestionMarkMethod {

	public static int predict(ClassificationInstance xmlResourceItem) {
		if (xmlResourceItem.getCleanText().contains("?"))
			return 1;	//	"Request"
		else
			return 0;	//	"Reply"
	}

}
