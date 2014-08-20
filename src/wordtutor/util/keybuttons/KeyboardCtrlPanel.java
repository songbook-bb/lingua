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
  static final String ex = "\u00A1";
  static final String qu = "\u00BF";
  static final String aa = "\u00E1";
  static final String ea = "\u00E9";
  static final String ia = "\u00ED";
  static final String na = "\u00F1";
  static final String oa = "\u00F3";
  static final String ua = "\u00FA";
  static final String uu = "\u00FC";
  // NIEMIECKIE
  static final String ss = "\u00DF";
  static final String od = "\u00F6";
  static final String ud = "\u00FC";
  static final String ad = "\u00E4";
  // POLSKIE
  static final String ap = "\u0105";
  static final String cp = "\u0107";
  static final String ep = "\u0119";
  static final String lp = " \u0142 ";
  static final String op = "\u00F3";
  static final String sp = "\u015B";
  static final String zp = "\u017C";
  static final String xp = "\u017A";
  // KASZUBSKIE
  static final String ak = "\u00E3";
  static final String ek = "\u00E9";
  static final String ec = "\u00EB";
  static final String ok = "\u00F2";
  static final String oc = "\u00F4";
  static final String uk = "\u00F9";
  // WŁOSKIE
  static final String aw = "\u00E0";
  static final String aww = "\u00E1";  
  static final String ew = "\u00E8";
  static final String ev = "\u00E9";
  static final String iw = "\u00EC";
  static final String iww = "\u00ED";  
  static final String ow = "\u00F2";
  static final String ov = "\u00F3";
  static final String uw= "\u00F9";
  static final String uww = "\u00FA";  
  // FRANCUSKIE
  static final String af = "\u00E0";
  static final String aff = "\u00E2";
  static final String cf = "\u00E7";
  static final String ef = "\u00E9";
  static final String eff = "\u00E8";
  static final String efff = "\u00EA";
  static final String effff = "\u00EB";  
  static final String iff = "\u00EE";
  static final String ifff = "\u00EF";  
  static final String of = "\u00F4";
  static final String uf = "\u00FB";
  static final String uff = "\u00F9";
  static final String ufff = "\u00FC"; 
  static final String yf = "\u00FF";
  static final String daf = "\u00E6";
  // CHORWACKIE
  static final String shr = "\u0161";
  static final String dhr = "\u0111";    
  static final String chr = "\u010D";
  static final String chrr = "\u0107";  
  static final String zhr = "\u017E";
  // LITEWSKIE
  static final String al = "\u0105";
  static final String cl = "\u010D";    
  static final String el = "\u0119";
  static final String ell = "\u0117";  
  static final String il = "\u012F";
  static final String sl = "\u0161";
  static final String ul = "\u0173";
  static final String ull = "\u016B";
  static final String zl = "\u017E";
  // NORWESKIE 
  static final String nae = "\u00E6";
  static final String no = "\u00F8";    
  static final String nna = "\u00E5";
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
      addButtonToPanel(qu, pLetters);
      addButtonToPanel(ex, pLetters);
      addButtonToPanel(aa, pLetters);
      addButtonToPanel(ea, pLetters);
      addButtonToPanel(ia, pLetters);
      addButtonToPanel(na, pLetters);
      addButtonToPanel(oa, pLetters);
      addButtonToPanel(ua, pLetters);
      addButtonToPanel(uu, pLetters);
    } else if (langEnum == KeyboardButtonType.GERMAN) {
      // NIEMIECKIE
      addButtonToPanel(ss, pLetters);
      addButtonToPanel(od, pLetters);
      addButtonToPanel(ud, pLetters);
      addButtonToPanel(ad, pLetters);
    } else if (langEnum == KeyboardButtonType.POLISH) {
      // POLSKIE
      addButtonToPanel(ap, pLetters);
      addButtonToPanel(cp, pLetters);
      addButtonToPanel(ep, pLetters);
      addButtonToPanel(lp, pLetters);
      addButtonToPanel(op, pLetters);
      addButtonToPanel(sp, pLetters);
      addButtonToPanel(zp, pLetters);
      addButtonToPanel(xp, pLetters);
    } else if (langEnum == KeyboardButtonType.KASHUBIAN) {
      // KASZUBSKIE
      addButtonToPanel(ak, pLetters);
      addButtonToPanel(ek, pLetters);
      addButtonToPanel(ec, pLetters);
      addButtonToPanel(ok, pLetters);
      addButtonToPanel(oc, pLetters);
      addButtonToPanel(uk, pLetters);
    } else if (langEnum == KeyboardButtonType.ITALIAN) {
      // WŁOSKIE
      addButtonToPanel(aw, pLetters);
      addButtonToPanel(aww, pLetters);      
      addButtonToPanel(ew, pLetters);
      addButtonToPanel(ev, pLetters);
      addButtonToPanel(iw, pLetters);
      addButtonToPanel(iww, pLetters);      
      addButtonToPanel(ow, pLetters);
      addButtonToPanel(ov, pLetters);
      addButtonToPanel(uw, pLetters);
      addButtonToPanel(uww, pLetters);      
    } else if (langEnum == KeyboardButtonType.FRENCH) {
      // FRANCUSKIE
      addButtonToPanel(af, pLetters);
      addButtonToPanel(aff, pLetters);
      addButtonToPanel(cf, pLetters);
      addButtonToPanel(ef, pLetters);
      addButtonToPanel(eff, pLetters);
      addButtonToPanel(efff, pLetters);
      addButtonToPanel(effff, pLetters);
      addButtonToPanel(iff, pLetters);
      addButtonToPanel(ifff, pLetters);
      addButtonToPanel(of, pLetters);
      addButtonToPanel(uf, pLetters);
      addButtonToPanel(uff, pLetters);
      addButtonToPanel(ufff, pLetters);
      addButtonToPanel(yf, pLetters);
      addButtonToPanel(daf, pLetters);
    } else if (langEnum == KeyboardButtonType.CROATIAN) {
        addButtonToPanel(shr, pLetters);
        addButtonToPanel(dhr, pLetters);
        addButtonToPanel(chr, pLetters);
        addButtonToPanel(chrr, pLetters);
        addButtonToPanel(zhr, pLetters);        
    } else if (langEnum == KeyboardButtonType.LITHUANIAN) {
    		addButtonToPanel(al, pLetters);
        addButtonToPanel(cl, pLetters);
        addButtonToPanel(el, pLetters);
        addButtonToPanel(ell, pLetters);
        addButtonToPanel(il, pLetters);        
        addButtonToPanel(sl, pLetters);
        addButtonToPanel(ul, pLetters);
        addButtonToPanel(ull, pLetters);
        addButtonToPanel(zl, pLetters);        
    } else if (langEnum == KeyboardButtonType.NORWEGIAN) {
  		addButtonToPanel(nae, pLetters);
      addButtonToPanel(no, pLetters);
      addButtonToPanel(nna, pLetters);    	
    }
    // ADD NEW KEYBOARD PANEL
  }

  private void addButtonToPanel(String buttonName, JPanel panel) {
    JButton button = new JButton(buttonName);
    button.setFocusable(false);
    if (buttonName == qu) {
      button.setToolTipText(Util.getLocalizedString("LESSON.BUTTON.QU.MARK"));
    } else if (buttonName == ex) {
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

