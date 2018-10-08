package com.emeralddb.base;

import java.util.EventObject;

import javax.swing.JTextArea;

public class ExceptionEvent extends EventObject {
	

	private JTextArea jTextArea;
	private long id;
	public ExceptionEvent( JTextArea jTextArea ) {
		super( jTextArea );
	}
	public Object getSource() {
		return jTextArea;
	}
	
}
