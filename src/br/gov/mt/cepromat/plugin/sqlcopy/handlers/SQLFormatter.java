package br.gov.mt.cepromat.plugin.sqlcopy.handlers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SQLFormatter extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SQLFormatter() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		ITextSelection texto = (ITextSelection) window.getActivePage().getActiveEditor().getEditorSite().getSelectionProvider().getSelection();
		String content = texto.getText();		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();

		StringSelection textoFormatado = new StringSelection(content + "-Alterado");
		clipboard.setContents(textoFormatado, null);
		
		MessageDialog.openInformation(
				window.getShell(),
				"SQLCopy",
				"Hello, Eclipse world = " + content);
		return null;
	}
}
