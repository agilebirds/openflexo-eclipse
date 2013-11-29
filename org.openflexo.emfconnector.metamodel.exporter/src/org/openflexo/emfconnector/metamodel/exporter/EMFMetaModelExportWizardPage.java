/** Copyright (c) 2013, THALES SYSTEMES AEROPORTES - All Rights Reserved
 * Author : Gilles Besançon
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or 
 * combining it with eclipse EMF (or a modified version of that library), 
 * containing parts covered by the terms of EPL 1.0, the licensors of this 
 * Program grant you additional permission to convey the resulting work.
 *
 * Contributors :
 *
 */
package org.openflexo.emfconnector.metamodel.exporter;

import java.util.Collection;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.Preferences;

public class EMFMetaModelExportWizardPage extends WizardPage {

	protected final Preferences preferences;
	protected String exportPath = null;
	protected EMFMetaModel emfMetaModel = null;

	protected EMFMetaModelExportWizardPage(Preferences preferences) {
		super("EMF Meta Model");
		setTitle(getName());
		this.preferences = preferences;
	}

	@Override
	public void createControl(final Composite parent) {
		GridLayout gridLayout1 = new GridLayout();
		parent.setLayout(gridLayout1);

		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout3 = new GridLayout(3, false);
		container.setLayout(gridLayout3);
		GridData containerGridData = new GridData(SWT.FILL, SWT.FILL, true,
				true);
		container.setLayoutData(containerGridData);

		// Export folder
		final String exportPathPreference = "exportpath";
		Label exportPathLabel = new Label(container, SWT.NONE);
		final Text exportPathText = new Text(container, SWT.BORDER);
		Button exportPathButton = new Button(container, SWT.PUSH);
		exportPathLabel.setText("Export Path :");
		exportPathLabel.setToolTipText("Export path");
		exportPathText.setText(preferences.get(exportPathPreference, ""));
		GridData textFillGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		textFillGridData.horizontalSpan = 1;
		exportPathText.setLayoutData(textFillGridData);
		exportPathText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				exportPath = ((Text) e.widget).getText();
				preferences.put(exportPathPreference, exportPath);
				setPageComplete(checkPageComplete());
			}
		});
		exportPathButton.setText("...");
		exportPathButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(parent.getShell(),
						SWT.SINGLE);
				// Default value preference.
				String preferenceValue = preferences.get(exportPathPreference,
						"");
				dialog.setFilterPath(preferenceValue);
				String path = dialog.open();
				if (path != null) {
					exportPathText.setText(path);
					preferences.put(exportPathPreference, path);
					exportPath = path;
				}
				setPageComplete(checkPageComplete());
			}
		});

		// MetaModel List
		ListViewer packageUriListViewer = new ListViewer(container, SWT.SINGLE
				| SWT.V_SCROLL);
		GridData packageUriListGridData = new GridData(SWT.FILL, SWT.FILL,
				true, true);
		packageUriListGridData.horizontalSpan = 3;
		packageUriListViewer.getList().setLayoutData(packageUriListGridData);
		packageUriListViewer
				.setContentProvider(new IStructuredContentProvider() {

					@Override
					public void inputChanged(Viewer viewer, Object oldInput,
							Object newInput) {
					}

					@Override
					public void dispose() {
					}

					@Override
					public Object[] getElements(Object inputElement) {
						return ((Collection<EMFMetaModel>) inputElement)
								.toArray();
					}
				});
		packageUriListViewer.setSorter(new ViewerSorter());
		packageUriListViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((EMFMetaModel) element).ePackageUri;
			}
		});
		packageUriListViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection selection = (IStructuredSelection) event
								.getSelection();
						if (selection.toList().size() > 0) {
							emfMetaModel = (EMFMetaModel) selection
									.getFirstElement();
						} else {
							emfMetaModel = null;
						}
						setPageComplete(checkPageComplete());
					}
				});
		packageUriListViewer.setInput(EMFMetaModelUtility.getEMFMetaModels());

		// Required to avoid an error in the system
		setControl(parent);
		setPageComplete(false);
	}

	protected boolean checkPageComplete() {
		return getEMFMetaModel() != null && getExportPath() != null;
	}

	public EMFMetaModel getEMFMetaModel() {
		return emfMetaModel;
	}

	public String getExportPath() {
		return exportPath;
	}
}
