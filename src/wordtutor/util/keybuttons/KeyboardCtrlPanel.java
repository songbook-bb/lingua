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
  final static String ex = "\u00A1";
  final static String qu = "\u00BF";
  final static String aa = "\u00E1";
  final static String ea = "\u00E9";
  final static String ia = "\u00ED";
  final static String na = "\u00F1";
  final static String oa = "\u00F3";
  final static String ua = "\u00FA";
  final static String uu = "\u00FC";
  // NIEMIECKIE
  final static String ss = "\u00DF";
  final static String od = "\u00F6";
  final static String ud = "\u00FC";
  final static String ad = "\u00E4";
  // POLSKIE
  final static String ap = "\u0105";
  final static String cp = "\u0107";
  final static String ep = "\u0119";
  final static String lp = " \u0142 ";
  final static String op = "\u00F3";
  final static String sp = "\u015B";
  final static String zp = "\u017C";
  final static String xp = "\u017A";
  // KASZUBSKIE
  final static String ak = "\u00E3";
  final static String ek = "\u00E9";
  final static String ec = "\u00EB";
  final static String ok = "\u00F2";
  final static String oc = "\u00F4";
  final static String uk = "\u00F9";
  // WŁOSKIE
  final static String aw = "\u00E0";
  final static String aww = "\u00E1";  
  final static String ew = "\u00E8";
  final static String ev = "\u00E9";
  final static String iw = "\u00EC";
  final static String iww = "\u00ED";  
  final static String ow = "\u00F2";
  final static String ov = "\u00F3";
  final static String uw= "\u00F9";
  final static String uww = "\u00FA";  
  // FRANCUSKIE
  final static String af = "\u00E0";
  final static String aff = "\u00E2";
  final static String cf = "\u00E7";
  final static String ef = "\u00E9";
  final static String eff = "\u00E8";
  final static String efff = "\u00EA";
  final static String effff = "\u00EB";  
  final static String iff = "\u00EE";
  final static String ifff = "\u00EF";  
  final static String of = "\u00F4";
  final static String uf = "\u00FB";
  final static String uff = "\u00F9";
  final static String ufff = "\u00FC"; 
  final static String yf = "\u00FF";
  final static String daf = "\u00E6";
  // CHORWACKIE
  final static String shr = "\u0161";
  final static String dhr = "\u0111";    
  final static String chr = "\u010D";
  final static String chrr = "\u0107";  
  final static String zhr = "\u017E";
  // LITEWSKIE
  final static String al = "\u0105";
  final static String cl = "\u010D";    
  final static String el = "\u0119";
  final static String ell = "\u0117";  
  final static String il = "\u012F";
  final static String sl = "\u0161";
  final static String ul = "\u0173";
  final static String ull = "\u016B";
  final static String zl = "\u017E";
  // NORWESKIE 
  final static String nae = "\u00E6";
  final static String no = "\u00F8";    
  final static String nna = "\u00E5";
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

