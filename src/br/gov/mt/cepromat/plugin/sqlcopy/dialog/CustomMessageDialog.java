package br.gov.mt.cepromat.plugin.sqlcopy.dialog;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CustomMessageDialog extends TitleAreaDialog {

	  private Text textFormattedSQL;
	  private String sql;

	  public CustomMessageDialog(Shell parentShell, String sql) {
	    super(parentShell);
	    this.sql = sql;
	  }

	  @Override
	  public void create() {
	    super.create();
	    setTitle("SQLCopy");
	    setMessage("O resultado da formatação está na área de transferência também. Basta aplicar \"Ctrl + v\"", IMessageProvider.INFORMATION);
	  }

	  @Override
	  protected Control createDialogArea(Composite parent) {
	    Composite area = (Composite) super.createDialogArea(parent);
	    Composite container = new Composite(area, SWT.NONE);
	    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    GridLayout layout = new GridLayout(2, false);
	    container.setLayout(layout);

	    createFormattedSQL(container);	    

	    return area;
	  }

	  private void createFormattedSQL(Composite container) {
	    Label labelFormattedSQL = new Label(container, SWT.NONE);
	    labelFormattedSQL.setText("Resultado:");

	    GridData gridDataResultado = new GridData();
	    gridDataResultado.grabExcessHorizontalSpace = true;
	    gridDataResultado.horizontalAlignment = GridData.FILL;
	    
	    textFormattedSQL = new Text(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);	    
	    textFormattedSQL.setLayoutData(gridDataResultado);
	    textFormattedSQL.setText(this.sql);	    
	  }

	  @Override
	  protected boolean isResizable() {
	    return true;
	  }
	}