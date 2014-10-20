package org.ossmeter.requestreplyclassifier.opennlptartarus.libsvm;

import java.util.List;

import uk.ac.nactem.posstemmer.OpenNlpTartarusSingleton;
import uk.ac.nactem.posstemmer.Token;

public class ClassificationInstance {
	
	private String bugTrackerId;
	private String bugId;
	private String commentId;
	
	private String url;
	private int articleNumber;
	private String subject;
	private List<List<Token>> tokenSentences;
	private List<List<Token>> cleanTokenSentences;

	private String text;
	private String cleanText;
	private String composedId;
	
	public  ClassificationInstance() {	}
	
	public String getComposedId() {
		if (composedId==null) setComposedId();
		return composedId;
	}

	private void setComposedId() {
		if ((bugTrackerId!=null)&&(bugId!=null)&&(commentId!=null))
			composedId = bugTrackerId+"#"+bugId+"#"+commentId;
		else if ((url!=null)&&(articleNumber!=0)) 
			composedId = url+"#"+articleNumber;
		else {
			System.err.println("Unable to compose ID");
		}
		toString();
	}

	public String getBugTrackerId() {
		return bugTrackerId;
	}
	
	public void setBugTrackerId(String bugTrackerId) {
		this.bugTrackerId = bugTrackerId;
		if (composedId!=null) setComposedId();
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		if (composedId!=null) setComposedId();
	}

	public String getBugId() {
		return bugId;
	}

	public void setBugId(String bugId) {
		this.bugId = bugId;
		if (composedId!=null) setComposedId();
	}


	public String getCommentId() {
		return commentId;
	}


	public void setCommentId(String commentId) {
		this.commentId = commentId;
		if (composedId!=null) setComposedId();
	}


	public int getArticleNumber() {
		return articleNumber;
	}
	
	public void setArticleNumber(int articleNumber) {
		this.articleNumber = articleNumber;
		if (composedId!=null) setComposedId();
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
		setCleanText();
	}
	
	public void setCleanText() {
		StringBuilder stringBuilder = new StringBuilder();
		for (String line: text.split("\n")) {
			String trimmedLine = line.trim();
			if ( (!trimmedLine.startsWith("<")) && (!trimmedLine.startsWith(">")) ) {
				stringBuilder.append(line);
				stringBuilder.append("\n");
			}
		}
		cleanText = stringBuilder.toString();
	}
	
	public String getCleanText() {
		return cleanText;
	}

	public List<List<Token>> getTokenSentences() {
		if (tokenSentences==null) {
			OpenNlpTartarusSingleton tartarus = OpenNlpTartarusSingleton.getInstance();
			tokenSentences = tartarus.getTagger().tag(text);
//			outputTag(tokenSentences);
		}
		return tokenSentences;
	}

	public List<List<Token>> getCleanTokenSentences() {
		if (cleanTokenSentences==null) {
			OpenNlpTartarusSingleton tartarus = OpenNlpTartarusSingleton.getInstance();
			cleanTokenSentences = tartarus.getTagger().tag(getCleanText());
//			outputTag(cleanTokenSentences);
		}
		return cleanTokenSentences;
	}

//	private void outputTag(List<List<Token>> tokenSentences) {
//	for (List<Token> tokens: tokenSentences)
//		for (Token token: tokens)
//			System.out.println(token.toString());
//		System.out.println();
//}

	@Override
	public String toString() {
		if (url!=null)
			return "ClassificationInstance "
					+ "[url=" + url + ", articleNumber=" 
					+ articleNumber + ", subject=" + subject + "]";
		else
			return "ClassificationInstance "
					+ "[bugTrackerId=" + bugTrackerId + ", bugId=" + bugId
					+ ", commentId=" + commentId + ", subject=" + subject + "]";
			
	}

}