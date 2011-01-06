/*
 * ProblemcView.java
 */
package dudge.problemc;

import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import dudge.problemc.binding.Problem;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import javax.xml.datatype.DatatypeFactory;

/**
 * The application's main frame.
 */
public class ProblemcView extends FrameView {

	File problemFile = null;
	Problem problem = new Problem();

	public ProblemcView(SingleFrameApplication app) {
		super(app);

		initComponents();

		newProblem();
	}

	public Problem getProblem() {
		return problem;
	}

	@Action
	public void showAboutBox() {
		if (aboutBox == null) {
			JFrame mainFrame = ProblemcApp.getApplication().getMainFrame();
			aboutBox = new ProblemcAboutBox(mainFrame);
			aboutBox.setLocationRelativeTo(mainFrame);
		}
		ProblemcApp.getApplication().show(aboutBox);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        mainPanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        desciptionPanel = new javax.swing.JPanel();
        problemTitle = new javax.swing.JTextField();
        problemAuthor = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        problemDescription = new javax.swing.JTextArea();
        testsPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        addTestButton = new javax.swing.JButton();
        removeTestButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        testsList = new javax.swing.JList();
        limitsPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        limitTime = new javax.swing.JTextField();
        limitMemory = new javax.swing.JTextField();
        limitOutput = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        testInput = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        testOutput = new javax.swing.JTextArea();
        openButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu problemMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        saveDialog = new javax.swing.JFileChooser();
        openDialog = new javax.swing.JFileChooser();

        mainPanel.setName("mainPanel"); // NOI18N

        jTabbedPane1.setMinimumSize(new java.awt.Dimension(0, 0));
        jTabbedPane1.setName("jTabbedPane1"); // NOI18N
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(640, 400));

        desciptionPanel.setName("desciptionPanel"); // NOI18N

        problemTitle.setName("problemTitle"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${problem.title}"), problemTitle, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        problemAuthor.setName("problemAuthor"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${problem.author}"), problemAuthor, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        problemDescription.setColumns(20);
        problemDescription.setRows(5);
        problemDescription.setName("problemDescription"); // NOI18N
        jScrollPane1.setViewportView(problemDescription);

        javax.swing.GroupLayout desciptionPanelLayout = new javax.swing.GroupLayout(desciptionPanel);
        desciptionPanel.setLayout(desciptionPanelLayout);
        desciptionPanelLayout.setHorizontalGroup(
            desciptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, desciptionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(desciptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
                    .addComponent(problemTitle, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
                    .addComponent(problemAuthor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE))
                .addContainerGap())
        );
        desciptionPanelLayout.setVerticalGroup(
            desciptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(desciptionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(problemTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(problemAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(dudge.problemc.ProblemcApp.class).getContext().getResourceMap(ProblemcView.class);
        jTabbedPane1.addTab(resourceMap.getString("desciptionPanel.TabConstraints.tabTitle"), desciptionPanel); // NOI18N

        testsPanel.setName("testsPanel"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        addTestButton.setText(resourceMap.getString("addTestButton.text")); // NOI18N
        addTestButton.setName("addTestButton"); // NOI18N
        addTestButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTestButtonActionPerformed(evt);
            }
        });

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(dudge.problemc.ProblemcApp.class).getContext().getActionMap(ProblemcView.class, this);
        removeTestButton.setAction(actionMap.get("removeTest")); // NOI18N
        removeTestButton.setFont(resourceMap.getFont("removeTestButton.font")); // NOI18N
        removeTestButton.setText(resourceMap.getString("removeTestButton.text")); // NOI18N
        removeTestButton.setName("removeTestButton"); // NOI18N
        removeTestButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeTestButtonActionPerformed(evt);
            }
        });

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        testsList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        testsList.setName("testsList"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${problem.tests.test}");
        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, testsList);
        jListBinding.setDetailBinding(org.jdesktop.beansbinding.ELProperty.create("Test ${number}"));
        bindingGroup.addBinding(jListBinding);

        jScrollPane4.setViewportView(testsList);

        limitsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("limitsPanel.border.title"))); // NOI18N
        limitsPanel.setName("limitsPanel"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        limitTime.setText(resourceMap.getString("limitTime.text")); // NOI18N
        limitTime.setName("limitTime"); // NOI18N

        limitMemory.setText(resourceMap.getString("limitMemory.text")); // NOI18N
        limitMemory.setName("limitMemory"); // NOI18N

        limitOutput.setText(resourceMap.getString("limitOutput.text")); // NOI18N
        limitOutput.setName("limitOutput"); // NOI18N

        javax.swing.GroupLayout limitsPanelLayout = new javax.swing.GroupLayout(limitsPanel);
        limitsPanel.setLayout(limitsPanelLayout);
        limitsPanelLayout.setHorizontalGroup(
            limitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(limitsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(limitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(limitsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(35, 35, 35)
                        .addComponent(limitTime, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE))
                    .addGroup(limitsPanelLayout.createSequentialGroup()
                        .addGroup(limitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(limitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(limitOutput, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                            .addComponent(limitMemory, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))))
                .addContainerGap())
        );
        limitsPanelLayout.setVerticalGroup(
            limitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(limitsPanelLayout.createSequentialGroup()
                .addGroup(limitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(limitTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(limitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(limitMemory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(limitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(limitOutput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(limitsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(addTestButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(removeTestButton))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addTestButton)
                    .addComponent(removeTestButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(limitsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridLayout(0, 1));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel3.border.title"))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        testInput.setColumns(20);
        testInput.setFont(resourceMap.getFont("testInput.font")); // NOI18N
        testInput.setRows(5);
        testInput.setName("testInput"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, testsList, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.input}"), testInput, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(testInput);

        jPanel3.add(jScrollPane2);

        jPanel2.add(jPanel3);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel4.border.title"))); // NOI18N
        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        testOutput.setColumns(20);
        testOutput.setFont(resourceMap.getFont("testOutput.font")); // NOI18N
        testOutput.setRows(5);
        testOutput.setName("testOutput"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, testsList, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.output}"), testOutput, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane3.setViewportView(testOutput);

        jPanel4.add(jScrollPane3);

        jPanel2.add(jPanel4);

        javax.swing.GroupLayout testsPanelLayout = new javax.swing.GroupLayout(testsPanel);
        testsPanel.setLayout(testsPanelLayout);
        testsPanelLayout.setHorizontalGroup(
            testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testsPanelLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
        );
        testsPanelLayout.setVerticalGroup(
            testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testsPanelLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(11, 11, 11))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(resourceMap.getString("testsPanel.TabConstraints.tabTitle"), testsPanel); // NOI18N

        openButton.setAction(actionMap.get("openProblem")); // NOI18N
        openButton.setText(resourceMap.getString("openButton.text")); // NOI18N
        openButton.setName("openButton"); // NOI18N

        saveButton.setAction(actionMap.get("saveProblem")); // NOI18N
        saveButton.setText(resourceMap.getString("saveButton.text")); // NOI18N
        saveButton.setName("saveButton"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap(350, Short.MAX_VALUE)
                .addComponent(openButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveButton)
                .addContainerGap())
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(openButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        problemMenu.setText(resourceMap.getString("problemMenu.text")); // NOI18N
        problemMenu.setName("problemMenu"); // NOI18N

        newMenuItem.setAction(actionMap.get("newProblem")); // NOI18N
        newMenuItem.setText(resourceMap.getString("newMenuItem.text")); // NOI18N
        newMenuItem.setName("newMenuItem"); // NOI18N
        problemMenu.add(newMenuItem);

        openMenuItem.setAction(actionMap.get("openProblem")); // NOI18N
        openMenuItem.setText(resourceMap.getString("openMenuItem.text")); // NOI18N
        openMenuItem.setName("openMenuItem"); // NOI18N
        problemMenu.add(openMenuItem);

        jSeparator1.setName("jSeparator1"); // NOI18N
        problemMenu.add(jSeparator1);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        problemMenu.add(exitMenuItem);

        menuBar.add(problemMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        saveDialog.setDialogTitle(resourceMap.getString("saveDialog.dialogTitle")); // NOI18N
        saveDialog.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        saveDialog.setFileFilter(new FileNameExtensionFilter("Problem files (*.problem)", "problem"));
        saveDialog.setName("saveDialog"); // NOI18N

        openDialog.setDialogTitle(resourceMap.getString("openDialog.dialogTitle")); // NOI18N
        openDialog.setFileFilter(new FileNameExtensionFilter("Problem files (*.problem)", "problem"));
        openDialog.setName("openDialog"); // NOI18N

        setComponent(mainPanel);
        setMenuBar(menuBar);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

	private void addTestButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTestButtonActionPerformed
		List<Problem.Tests.Test> tests = problem.getTests().getTest();
		int maxNumber = 0;
		for (Problem.Tests.Test test : tests) {
			if (test.getNumber() > maxNumber) {
				maxNumber = test.getNumber();
			}
		}

		Problem.Tests.Test test = new Problem.Tests.Test();
		test.setNumber(maxNumber + 1);

		test.setInput("");
		test.setOutput("");

		tests.add(test);
	}//GEN-LAST:event_addTestButtonActionPerformed

	private void removeTestButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeTestButtonActionPerformed
		List<Problem.Tests.Test> tests = problem.getTests().getTest();

		ArrayList<Problem.Tests.Test> testsToDelete = new ArrayList<Problem.Tests.Test>();

		for (int selectedIndex : testsList.getSelectedIndices()) {
			testsToDelete.add(tests.get(selectedIndex));
		}

		for (Problem.Tests.Test test : testsToDelete) {
			tests.remove(test);
		}
	}//GEN-LAST:event_removeTestButtonActionPerformed

	@Action
	public void newProblem() {
		problemFile = null;
		saveButton.setText("Save as...");

		Problem oldProblem = problem;

		problem = new Problem();
		problem.setFormatVersion(1);
		problem.setTitle("Title");
		problem.setAuthor("Author");

		Problem.Tests.Test test = new Problem.Tests.Test();
		test.setNumber(1);
		test.setInput("foobar");
		test.setOutput("grr");

		Problem.Tests tests = new Problem.Tests();
		tests.getTest().add(test);
		problem.setTests(tests);

		for(PropertyChangeListener listener : this.getPropertyChangeListeners())
		{
			listener.propertyChange(new PropertyChangeEvent(this, "problem", oldProblem, problem));
		}
	}
	
	@Action
	public void saveProblem() {

		File file;

		if(problemFile == null)
		{
			if(saveDialog.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
			{
				return;
			}

			file = saveDialog.getSelectedFile();
		}
		else
		{
			file = problemFile;
		}

		try {
			problem.setCreated(DatatypeFactory.newInstance().newXMLGregorianCalendar(
					new GregorianCalendar(TimeZone.getTimeZone("GMT+0"))));

			Problem.Limits limits = new Problem.Limits();
			limits.setTime(Long.parseLong(limitTime.getText()));
			limits.setMemory(Long.parseLong(limitMemory.getText()));
			limits.setOutput(Long.parseLong(limitOutput.getText()));
			problem.setLimits(limits);

			OutputStream outputStream = new FileOutputStream(file);

			javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(problem.getClass().getPackage().getName());
			javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
			marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
			marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(problem, outputStream);

			outputStream.close();

			problemFile = file;
			saveButton.setText("Save");
		} catch (Exception ex) {
			java.util.logging.Logger.getLogger(this.getClass().toString())
					.log(Level.WARNING, ex.getStackTrace().toString());
			JOptionPane.showMessageDialog(mainPanel, ex.toString(), "Save failed", JOptionPane.ERROR_MESSAGE);
		}

		/*String schemaLang = "http://www.w3.org/2001/XMLSchema";

		SchemaFactory factory = SchemaFactory.newInstance(schemaLang);
		Schema schema = factory.newSchema(new StreamSource("sample.xsd"));
		Validator validator = schema.newValidator();
		 */

	}

	@Action
	public void openProblem() {
		if(openDialog.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		File file = openDialog.getSelectedFile();

		try {
			InputStream inputStream = new FileInputStream(file);

			javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(problem.getClass().getPackage().getName());
			javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
			Problem oldProblem = this.problem;
			this.problem = (Problem) unmarshaller.unmarshal(inputStream);
			inputStream.close();

			problemFile = file;
			saveButton.setText("Save");

			for(PropertyChangeListener listener : this.getPropertyChangeListeners())
			{
				listener.propertyChange(new PropertyChangeEvent(this, "problem", oldProblem, problem));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(this.getClass().toString())
					.log(Level.WARNING, ex.getStackTrace().toString());
			JOptionPane.showMessageDialog(mainPanel, ex.toString(), "Open failed", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Action
	public void removeTest() {
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addTestButton;
    private javax.swing.JPanel desciptionPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField limitMemory;
    private javax.swing.JTextField limitOutput;
    private javax.swing.JTextField limitTime;
    private javax.swing.JPanel limitsPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JButton openButton;
    private javax.swing.JFileChooser openDialog;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JTextField problemAuthor;
    private javax.swing.JTextArea problemDescription;
    private javax.swing.JTextField problemTitle;
    private javax.swing.JButton removeTestButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JFileChooser saveDialog;
    private javax.swing.JTextArea testInput;
    private javax.swing.JTextArea testOutput;
    private javax.swing.JList testsList;
    private javax.swing.JPanel testsPanel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
	private JDialog aboutBox;
}
