package scala.tools.eclipse
package properties

import scala.collection.mutable.ListBuffer
import scala.tools.eclipse.ScalaPlugin
import scala.tools.eclipse.util.SWTUtils.CheckBox

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer
import org.eclipse.jface.preference.ColorFieldEditor
import org.eclipse.jface.preference.PreferencePage
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control
import org.eclipse.swt.widgets.Group
import org.eclipse.ui.IWorkbench
import org.eclipse.ui.IWorkbenchPreferencePage

import EditorPreferencePage._

class EditorPreferencePage extends PreferencePage with IWorkbenchPreferencePage {

  private val store = ScalaPlugin.prefStore

  private val preferencesToSave = ListBuffer[() => Unit]()

  override def performOk(): Boolean = {
    preferencesToSave foreach (_())
    super.performOk()
  }

  override def init(workbench: IWorkbench): Unit = {}

  override def createContents(parent: Composite): Control = {
    setPreferenceStore(store)

    val base = new Composite(parent, SWT.NONE)
    base.setLayout(new GridLayout(1, true))
    base.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true))

    createSettingsGroup(base)
    createIndentGuideGroup(base)

    base
  }

  private def createSettingsGroup(base: Composite): Unit = {
    val surround = group("Automatically surround selection", base)
    checkBox(P_ENABLE_SMART_BRACKETS, "With [brackets]", surround)
    checkBox(P_ENABLE_SMART_BRACES, "With {braces}", surround)
    checkBox(P_ENABLE_SMART_PARENS, "With (parenthesis)", surround)
    checkBox(P_ENABLE_SMART_QUOTES, "With \"quotes\"", surround)

    val typing = group("Typing", base)
    checkBox(P_ENABLE_AUTO_CLOSING_BRACES, "Enable auto closing braces when editing an existing line", typing)
    checkBox(P_ENABLE_AUTO_CLOSING_COMMENTS, "Automatically close multi line comments and Scaladoc", typing)
    checkBox(P_ENABLE_AUTO_ESCAPE_LITERALS, "Automatically escape \" signs in string literals", typing)
    checkBox(P_ENABLE_AUTO_ESCAPE_SIGN, "Automatically escape \\ signs in string and character literals", typing)
    checkBox(P_ENABLE_AUTO_REMOVE_ESCAPED_SIGN, "Automatically remove complete escaped sign in string and character literals", typing)

    val highlighting = group("Highlighting", base)
    checkBox(P_ENABLE_MARK_OCCURRENCES, "Mark Occurences of the selected element in the current file", highlighting)
  }

  private def group(text: String, parent: Composite): Group = {
    val g = new Group(parent, SWT.NONE)
    g.setText(text)
    g.setLayout(new GridLayout(1, true))
    g.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false))
    g
  }

  private def checkBox(preference: String, labelText: String, parent: Composite): CheckBox = {
    val b = new CheckBox(store, preference, labelText, parent)
    preferencesToSave += { () => b.store() }
    b
  }

  private def createIndentGuideGroup(base: Composite): Unit = {
    val indentGuide = group("Indent Guide", base)
    val enable = checkBox(INDENT_GUIDE_ENABLE, "Enable the indent guide", indentGuide)
    val color = new Composite(indentGuide, SWT.NONE)
    val c = new ColorFieldEditor(INDENT_GUIDE_COLOR, "Color:", color)

    c.setPreferenceStore(store)
    c.load()
    preferencesToSave += { () => c.store() }

    def enableControls(b: Boolean) = c.setEnabled(b, color)

    enable += (_ => enableControls(enable.isChecked))
    enableControls(enable.isChecked)
  }

}

object EditorPreferencePage {
  private final val BASE = "scala.tools.eclipse.editor."

  final val P_ENABLE_SMART_BRACKETS = BASE + "smartBrackets"
  final val P_ENABLE_SMART_BRACES = BASE + "smartBraces"
  final val P_ENABLE_SMART_PARENS = BASE + "smartParens"
  final val P_ENABLE_SMART_QUOTES = BASE + "smartQuotes"

  final val P_ENABLE_AUTO_CLOSING_BRACES = BASE + "autoClosingBrace"
  final val P_ENABLE_AUTO_CLOSING_COMMENTS = BASE + "autoClosingComments"
  final val P_ENABLE_AUTO_ESCAPE_LITERALS = BASE + "autoEscapeLiterals"
  final val P_ENABLE_AUTO_ESCAPE_SIGN = BASE + "autoEscapeSign"
  final val P_ENABLE_AUTO_REMOVE_ESCAPED_SIGN = BASE + "autoRemoveEscapedSign"

  final val P_ENABLE_MARK_OCCURRENCES = BASE + "markOccurences"

  final val INDENT_GUIDE_ENABLE = BASE + "indentGuideEnable"
  final val INDENT_GUIDE_COLOR = BASE + "indentGuideColor"
}

class EditorPreferenceInitializer extends AbstractPreferenceInitializer {

  override def initializeDefaultPreferences() {
    val store = ScalaPlugin.plugin.getPreferenceStore
    store.setDefault(P_ENABLE_SMART_BRACKETS, false)
    store.setDefault(P_ENABLE_SMART_BRACES, false)
    store.setDefault(P_ENABLE_SMART_PARENS, false)
    store.setDefault(P_ENABLE_SMART_QUOTES, false)

    store.setDefault(P_ENABLE_AUTO_CLOSING_BRACES, true)
    store.setDefault(P_ENABLE_AUTO_CLOSING_COMMENTS, true)
    store.setDefault(P_ENABLE_AUTO_ESCAPE_LITERALS, false)
    store.setDefault(P_ENABLE_AUTO_ESCAPE_SIGN, false)
    store.setDefault(P_ENABLE_AUTO_REMOVE_ESCAPED_SIGN, false)

    store.setDefault(P_ENABLE_MARK_OCCURRENCES, false)

    store.setDefault(INDENT_GUIDE_ENABLE, false)
    store.setDefault(INDENT_GUIDE_COLOR, "72,72,72")
  }
}