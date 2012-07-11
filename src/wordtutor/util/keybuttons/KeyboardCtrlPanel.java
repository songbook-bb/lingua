/** Tomek
 *
 */
package wordtutor.util.keybuttons;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import wordtutor.utils.Util;

public class KeyboardCtrlPanel {

  public static KeyboardButtonType langValue;
  Logger logger = Logger.getLogger(KeyboardCtrlPanel.class);
  // HISZPAŃSKIE
  final String EX = "\u00A1";
  final String QU = "\u00BF";
  final String AA = "\u00E1";
  final String EA = "\u00E9";
  final String IA = "\u00ED";
  final String NA = "\u00F1";
  final String OA = "\u00F3";
  final String UA = "\u00FA";
  final String UU = "\u00FC";
  // NIEMIECKIE
  final String SS = "\u00DF";
  final String OD = "\u00F6";
  final String UD = "\u00FC";
  final String AD = "\u00E4";
  // POLSKIE
  final String AP = "\u0105";
  final String CP = "\u0107";
  final String EP = "\u0119";
  final String LP = " \u0142 ";
  final String OP = "\u00F3";
  final String SP = "\u015B";
  final String ZP = "\u017C";
  final String XP = "\u017A";
  // KASZUBSKIE
  final String AK = "\u00E3";
  final String EK = "\u00E9";
  final String EC = "\u00EB";
  final String OK = "\u00F2";
  final String OC = "\u00F4";
  final String UK = "\u00F9";
  // WŁOSKIE
  final String AW = "\u00E0";
  final String AWW = "\u00E1";  
  final String EW = "\u00E8";
  final String EV = "\u00E9";
  final String IW = "\u00EC";
  final String IWW = "\u00ED";  
  final String OW = "\u00F2";
  final String OV = "\u00F3";
  final String UW = "\u00F9";
  final String UWW = "\u00FA";  
  // FRANCUSKIE
  final String AF = "\u00E0";
  final String AFF = "\u00E2";
  final String CF = "\u00E7";
  final String EF = "\u00E9";
  final String EFF = "\u00E8";
  final String EFFF = "\u00EA";
  final String EFFFF = "\u00EB";  
  final String IF = "\u00EE";
  final String IFF = "\u00EF";  
  final String OF = "\u00F4";
  final String UF = "\u00FB";
  final String UFF = "\u00F9";
  final String UFFF = "\u00FC"; 
  final String YF = "\u00FF";
  final String DAF = "\u00E6";
  // CHORWACKIE
  final String SHR = "\u0161";
  final String DHR = "\u0111";    
  final String CHR = "\u010D";
  final String CHRR = "\u0107";  
  final String ZHR = "\u017E";
  // NEW KEYBOARD KEYS DEFINITION HERE
  private final JPanel pLetters;

  /**
   * Adds listener to all buttons
   * 
   * @param buttonListener
   *          class
   */
  public void addListenerToAll(KeyboardBtnListener buttonListener) {
    Component[] compArray = pLetters.getComponents();
    for (Component comp : compArray) {
      if (comp.getClass().equals(new JButton().getClass())) {
        JButton b = (JButton) comp;
        b.addActionListener(buttonListener);
        // logger.debug("NAME:"+b.getSource());
      }
    }
  }

  public KeyboardCtrlPanel(KeyboardButtonType langEnum) {
    pLetters = new JPanel();
    pLetters.setLayout(new BoxLayout(pLetters, BoxLayout.X_AXIS));

    if (langEnum == KeyboardButtonType.SPANISH) {
      // HISZPAŃSKIE
      addButtonToPanel(QU, pLetters);
      addButtonToPanel(EX, pLetters);
      addButtonToPanel(AA, pLetters);
      addButtonToPanel(EA, pLetters);
      addButtonToPanel(IA, pLetters);
      addButtonToPanel(NA, pLetters);
      addButtonToPanel(OA, pLetters);
      addButtonToPanel(UA, pLetters);
      addButtonToPanel(UU, pLetters);
    } else if (langEnum == KeyboardButtonType.GERMAN) {
      // NIEMIECKIE
      addButtonToPanel(SS, pLetters);
      addButtonToPanel(OD, pLetters);
      addButtonToPanel(UD, pLetters);
      addButtonToPanel(AD, pLetters);
    } else if (langEnum == KeyboardButtonType.POLISH) {
      // POLSKIE
      addButtonToPanel(AP, pLetters);
      addButtonToPanel(CP, pLetters);
      addButtonToPanel(EP, pLetters);
      addButtonToPanel(LP, pLetters);
      addButtonToPanel(OP, pLetters);
      addButtonToPanel(SP, pLetters);
      addButtonToPanel(ZP, pLetters);
      addButtonToPanel(XP, pLetters);
    } else if (langEnum == KeyboardButtonType.KASHUBIAN) {
      // KASZUBSKIE
      addButtonToPanel(AK, pLetters);
      addButtonToPanel(EK, pLetters);
      addButtonToPanel(EC, pLetters);
      addButtonToPanel(OK, pLetters);
      addButtonToPanel(OC, pLetters);
      addButtonToPanel(UK, pLetters);
    } else if (langEnum == KeyboardButtonType.ITALIAN) {
      // WŁOSKIE
      addButtonToPanel(AW, pLetters);
      addButtonToPanel(AWW, pLetters);      
      addButtonToPanel(EW, pLetters);
      addButtonToPanel(EV, pLetters);
      addButtonToPanel(IW, pLetters);
      addButtonToPanel(IWW, pLetters);      
      addButtonToPanel(OW, pLetters);
      addButtonToPanel(OV, pLetters);
      addButtonToPanel(UW, pLetters);
      addButtonToPanel(UWW, pLetters);      
    } else if (langEnum == KeyboardButtonType.FRENCH) {
      // FRANCUSKIE
      addButtonToPanel(AF, pLetters);
      addButtonToPanel(AFF, pLetters);
      addButtonToPanel(CF, pLetters);
      addButtonToPanel(EF, pLetters);
      addButtonToPanel(EFF, pLetters);
      addButtonToPanel(EFFF, pLetters);
      addButtonToPanel(EFFFF, pLetters);
      addButtonToPanel(IF, pLetters);
      addButtonToPanel(IFF, pLetters);
      addButtonToPanel(OF, pLetters);
      addButtonToPanel(UF, pLetters);
      addButtonToPanel(UFF, pLetters);
      addButtonToPanel(UFFF, pLetters);
      addButtonToPanel(YF, pLetters);
      addButtonToPanel(DAF, pLetters);
    } else if (langEnum == KeyboardButtonType.CROATIAN) {
        addButtonToPanel(SHR, pLetters);
        addButtonToPanel(DHR, pLetters);
        addButtonToPanel(CHR, pLetters);
        addButtonToPanel(CHRR, pLetters);
        addButtonToPanel(ZHR, pLetters);        
    }
    // ADD NEW KEYBOARD PANEL
  }

  private void addButtonToPanel(String buttonName, JPanel panel) {
    JButton button = new JButton(buttonName);
    button.setFocusable(false);
    if (buttonName == QU) {
      button.setToolTipText(Util.getLocalizedString("LESSON.BUTTON.QU.MARK"));
    } else if (buttonName == EX) {
      button.setToolTipText(Util.getLocalizedString("LESSON.BUTTON.EX.MARK"));
    }
    panel.add(button);
  }

  public JPanel getPanel() {
    return pLetters;
  }

  public static void main(String args[]) {    
	  
  }

}

