/*******************************************************************************
 * Copyright (c) 2014 OSSMETER Partners.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juri Di Rocco - Implementation.
 *    Davide Di Ruscio - Implementation
 *******************************************************************************/
package org.ossmeter.platform.communicationchannel.zendesk;

import java.text.SimpleDateFormat;
import java.util.List;

import org.ossmeter.platform.Date;
import org.ossmeter.platform.communicationchannel.zendesk.model.Ticket;
import org.ossmeter.platform.delta.communicationchannel.CommunicationChannelArticle;
import org.ossmeter.platform.delta.communicationchannel.CommunicationChannelDelta;
import org.ossmeter.platform.delta.communicationchannel.ICommunicationChannelManager;
import org.ossmeter.repository.model.CommunicationChannel;
import org.ossmeter.repository.model.cc.zendesk.Zendesk;

import com.mongodb.DB;

public class ZendeskManager implements ICommunicationChannelManager<Zendesk> {

	private final static int RETRIEVAL_STEP = 50;

	@Override
	public boolean appliesTo(CommunicationChannel communicationChannel) {
		return communicationChannel instanceof Zendesk;
	}

	@Override
	public CommunicationChannelDelta getDelta(DB db,
			Zendesk communicationChannel, Date date) throws Exception {

		org.ossmeter.platform.communicationchannel.zendesk.Zendesk zendesk;
		zendesk = new org.ossmeter.platform.communicationchannel.zendesk.Zendesk.Builder(
				communicationChannel.getUrl())
				.setUsername(communicationChannel.getUsername())
				.setPassword(communicationChannel.getPassword()).build();
		Ticket lastTicket = (Ticket) zendesk
				.getSearchResults(Ticket.class, "query=type:ticket",
						"&sort_by=updated_at&sort_order=desc").iterator()
				.next();
		CommunicationChannelDelta delta = new CommunicationChannelDelta();
		delta.setNewsgroup(communicationChannel);
		if (lastTicket != null) {
			long lastTicketId = lastTicket.getId();
			String lac = communicationChannel.getLastArticleChecked();
			if (lac == null || lac.equals("") || lac.equals("null"))
				lac = "1";
			long lastTicketChecked = Long.parseLong(lac);

			long retrievalStep = RETRIEVAL_STEP;
			Boolean dayCompleted = false;
			while (!dayCompleted) {
				if (lastTicketChecked + retrievalStep > lastTicketId) {
					retrievalStep = lastTicketId - lastTicketChecked;
					dayCompleted = true;
				}
				List<Ticket> tickets = null;
				Date articleDate = date;
				// The following loop discards messages for days earlier than
				// the required one.
				do {
					tickets = zendesk.getTickets(lastTicketChecked,
							lastTicketChecked + retrievalStep);
					if (tickets.size() > 0) {
						Ticket lastArticleRetrieved = tickets.get(tickets
								.size() - 1);
						java.util.Date javaArticleDate = lastArticleRetrieved
								.getUpdatedAt();
						articleDate = new Date(javaArticleDate);
						date = articleDate;
						if (date.compareTo(articleDate) > 0)
							lastTicketChecked = lastArticleRetrieved.getId();
					}
				} while (date.compareTo(articleDate) > 0);
				for (Ticket tk : tickets) {
					java.util.Date javaTicketDate = tk.getUpdatedAt();
					if (javaTicketDate != null) {
						articleDate = new Date(javaTicketDate);
						if (date.compareTo(articleDate) < 0) {
							dayCompleted = true;
							// System.out.println("dayCompleted");
						} else if (date.compareTo(articleDate) == 0) {
							CommunicationChannelArticle communicationChannelArticle = new CommunicationChannelArticle();
							communicationChannelArticle.setArticleId(tk.getId()
									+ "");
							int i = Integer.parseInt(tk.getId() + "");
							communicationChannelArticle.setArticleNumber(i);
							communicationChannelArticle.setDate(javaTicketDate);
							// I haven't seen any messageThreadIds on NNTP
							// servers, yet.
							// communicationChannelArticle.setMessageThreadId(article.messageThreadId());
							org.ossmeter.repository.model.cc.zendesk.Zendesk zenDesk = new org.ossmeter.repository.model.cc.zendesk.Zendesk();
							zenDesk.setUrl(communicationChannel.getUrl());
							zenDesk.setAuthenticationRequired(communicationChannel
									.getAuthenticationRequired());
							zenDesk.setUsername(communicationChannel
									.getUsername());
							zenDesk.setPassword(communicationChannel
									.getPassword());
							zenDesk.setNewsGroupName(communicationChannel
									.getNewsGroupName());
							zenDesk.setPort(communicationChannel.getPort());
							zenDesk.setInterval(communicationChannel
									.getInterval());
							// communicationChannelArticle.setNewsgroup(zenDesk);
							communicationChannelArticle.setUser(zendesk
									.getUser(tk.getRequesterId()).getName());
							communicationChannelArticle.setSubject(tk
									.getSubject());

							communicationChannelArticle.setUser(zendesk
									.getUser(tk.getRequesterId()).getName());

							communicationChannelArticle.setText(tk
									.getDescription());
							delta.getArticles()
									.add(communicationChannelArticle);
							lastTicketChecked = tk.getId();
							// System.out.println("dayNOTCompleted");
						} else {

							// This means that the deltas of those article dates
							// are incomplete,
							// i.e. the deltas did not contain all articles of
							// those dates.
						}
					} else {
						// If an article has no correct date, then ignore it
						System.err.println("\t\tUnparsable article date: "
								+ tk.getUpdatedAt());
					}
				}

			}
			communicationChannel.setLastArticleChecked(lastTicketChecked + "");

		}
		zendesk.close();
		System.out.println("delta (" + date.toString() + ") contains:\t"
				+ delta.getArticles().size() + " nntp articles");

		return delta;

	}

	@Override
	public String getContents(DB db, Zendesk communicationChannel,
			CommunicationChannelArticle article) throws Exception {

		org.ossmeter.platform.communicationchannel.zendesk.Zendesk zendesk;
		zendesk = new org.ossmeter.platform.communicationchannel.zendesk.Zendesk.Builder(
				communicationChannel.getUrl())
				.setUsername(communicationChannel.getUsername())
				.setPassword(communicationChannel.getPassword()).build();
		String contents = zendesk.getTicket(article.getArticleNumber())
				.getDescription();
		zendesk.close();
		return contents;
	}

	@Override
	public Date getFirstDate(DB db, Zendesk communicationChannel)
			throws Exception {
		org.ossmeter.platform.communicationchannel.zendesk.Zendesk zendesk;
		zendesk = new org.ossmeter.platform.communicationchannel.zendesk.Zendesk.Builder(
				communicationChannel.getUrl())
				.setUsername(communicationChannel.getUsername())
				.setPassword(communicationChannel.getPassword()).build();
		if (zendesk.getTickets().iterator().hasNext()) {
			Ticket t = zendesk.getTickets().iterator().next();
			int firstArticleNumber = Integer.parseInt(t.getId() + "");

			if (firstArticleNumber == 0) {
				return null; // This is to deal with message-less newsgroups.
			}

			zendesk.close();

			return new Date(t.getUpdatedAt());
		}
		return null;
	}

	static SimpleDateFormat[] sdfList = new SimpleDateFormat[] {
			new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz"),
			new SimpleDateFormat("dd MMM yyyy HH:mm:ss zzz"),
			new SimpleDateFormat("EEE, dd MMM yyyy HH:mm zzz (Z)"),
			new SimpleDateFormat("EEE, dd MMM yyyy HH:mm zzz"),
			new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss"),
			new SimpleDateFormat("dd MMM yyyy HH:mm:ss"),
			new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z"),
			new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy"),
			new SimpleDateFormat("yyyyMMdd") };
}
