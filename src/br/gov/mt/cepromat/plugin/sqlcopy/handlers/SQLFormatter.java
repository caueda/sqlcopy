package br.gov.mt.cepromat.plugin.sqlcopy.handlers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

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
		ITextSelection content = (ITextSelection) window.getActivePage().getActiveEditor().getEditorSite().getSelectionProvider().getSelection();
		String texto = content.getText();		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		
		String sqlFormatado = formatSQL(texto);
		
		StringSelection textoFormatado = new StringSelection(sqlFormatado);
		clipboard.setContents(textoFormatado, null);
		
		if(isBlank(texto)){
			MessageDialog.openInformation(
					window.getShell(),
					"SQLCopy",
					"Você não selecionou nada \"cabeça\" !");			
		} else {			
			if(isConvertToJava(texto)){//Realizar a substituição do texto selecionado apenas quando estiver convertendo um SQL para Java.
				IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();			
				ITextEditor editor = (ITextEditor)part;
				IDocumentProvider prov = editor.getDocumentProvider();
		        IDocument doc = prov.getDocument( editor.getEditorInput() );
		        ISelection sel = editor.getSelectionProvider().getSelection();
		        
		        if ( sel instanceof TextSelection ) {
		            final TextSelection textSel = (TextSelection)sel;
		            try {
						doc.replace( textSel.getOffset(), textSel.getLength(), sqlFormatado );
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
		        }
			}
			MessageDialog.openInformation(
					window.getShell(),
					"SQLCopy",
					"SQL Formatado!");
		}
		
		return null;
	}
	
	private String[] startWithKeyWords = new String[]{
			"select","update","insert","delete","with",
			"begin","create","drop","truncate","alter",
			"grant","exec","call"
	};
	
	private boolean isConvertToJava(String texto){
		String lower = texto.trim().toLowerCase();
		return startWith(lower, startWithKeyWords);
	}
	
	/**
	 * Método para formatar SQL para Java StringBuilder.
	 * @param texto
	 * @return
	 */
	private String formatSQL(String texto){
		String formatar = texto.trim();
		String lower = formatar.toLowerCase();
		String resultado = "";
		if(startWith(lower, startWithKeyWords)){
			String[] linhas = formatar.split("\n");
			StringBuilder newsql = new StringBuilder();
			newsql.append("StringBuilder sql = new StringBuilder();\n");
			for(String linha: linhas){
				newsql.append("sql.append(\"" + linha.replace("\n", "").replace("\r", "") + "\\n\");\n" );
			}
			resultado = newsql.toString();
		} else if(contains(lower, new String[]{".append","stringbuilder","stringbuffer"})){									
			resultado = removeJavaStringBuilderPart(formatar);
		} else {
			resultado = removeStringPart(formatar);
		}
		return resultado;
	}
	
	/**
	 * Método que identifica se é um comando SQL.
	 * @param sql
	 * @param comparar
	 * @return
	 */
	private boolean startWith(String sql, String[] comparar){
		for(String comp : comparar){
			if(sql.startsWith(comp)) return true;
		}
		return false;
	}
	
	private boolean contains(String sql, String[] comparar){
		for(String comp : comparar){
			if(sql.contains(comp)) return true;
		}
		return false;
	}
	
	private boolean isBlank(String value){
		return (value == null || value.trim().isEmpty());
	}
	
	public String removeJavaStringBuilderPart(String sql) {
		StringBuilder formatado = new StringBuilder();
		String[] linhas = sql.split("\n");
		for(String linha : linhas){
			if(linha.contains("StringBuilder") || linha.contains("StringBuffer")){
				linha = linha.replaceAll("new\\s*StringBuilder\\s*\\(", "").replaceAll("\\s*\\)\\s*;$", "");
				linha = linha.replaceAll("(StringBuilder|StringBuffer)\\s+.+\\s+=\\s+new\\s+", "");
				linha = linha.replace("\"","");
			}
			//System.out.println(l.replace("sql.append(\"", ""));
			linha = linha.replace("\\n", "").replace("\\t", "").replace("\n", "").replace("\t", "");
			String firstPart = linha.replaceAll("\\s*.+\\.append\\s*\\(\\s*"," ").replaceAll("\"$", "");
			//System.out.println(firstPart);			
			firstPart = removeStringPart(firstPart.replaceAll("\"\\s*\\)\\s*;$", " "));
			formatado.append(firstPart);
		}
		return formatado.toString();
	}
	
	/**
	 * Método utilizado para remover o comando SQL de uma String Java.
	 * @param sql
	 * @return
	 */
	public String removeStringPart(String sql){
		StringBuilder resultado = new StringBuilder();
		String[] linhas = sql.split("\n");
		for(String linha : linhas){
			if(linha.trim().startsWith("//")){
				linha = linha.replace("//", "--");
			} else if(linha.contains("String")){
				linha = linha.replaceAll("\\s*(String)\\s*.+\\s*=\\s*","").replaceAll("new\\s+String\\(","");
				linha = linha.replaceAll("\\)\\s*;$","");				
			}
			linha = linha.replaceAll("\\)\\s*;","").replaceAll("\\s*\\+\\s*$", "");
			linha = linha.replace("\\n","").replace("\\r", "").replace("\n", "").replace("\r", "");
			linha = linha.replaceAll("\\+*\\s*\"","").replaceAll("\\s*\"\\s*;$", "");
			resultado.append(linha).append("\n");
		}
		return resultado.toString();
	}
}
