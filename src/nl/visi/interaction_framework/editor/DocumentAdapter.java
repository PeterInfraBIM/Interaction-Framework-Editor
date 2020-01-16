/**
 * Copyright 2008-2010 COINS
 * No part of this code may be reproduced, stored in a retrieval system, or transmitted  
 * in any form or by any means, electronic, mechanical, photocopying, recording or otherwise, 
 * without the prior written permission of COINS.
 *
 * Created on 22 mei 2008
 */
package nl.visi.interaction_framework.editor;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Peter Willems (COINS/TNO)
 * 
 */
abstract class DocumentAdapter implements DocumentListener {

	/**
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void changedUpdate(DocumentEvent e) {
		update(e);
	}

	/**
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void insertUpdate(DocumentEvent e) {
		update(e);
	}

	/**
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void removeUpdate(DocumentEvent e) {
		update(e);
	}

	/**
	 * Algorithm for each change in the document.
	 * 
	 * @param e
	 */
	protected abstract void update(DocumentEvent e);

}
