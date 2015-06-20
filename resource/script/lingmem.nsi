; lingmem.nsi
;
; This script is based on example1.nsi, but it remember the directory, 
; has uninstall support and (optionally) installs start menu shortcuts.
;
; It will install example2.nsi into a directory that the user selects,

;--------------------------------

; The name of the installer
Name "LingMEM"

; The file to write
OutFile "LingMEM-v1-25.exe"

; The default installation directory
#InstallDir $PROGRAMFILES\LingMEM
InstallDir C:\LingMEM
#InstallDir $DESKTOP\LingMEM

; Registry key to check for directory (so if you install again, it will 
; overwrite the old one automatically)
InstallDirRegKey HKLM "Software\NSIS_LingMEM" "Install_Dir"

; Request application privileges for Windows Vista
RequestExecutionLevel admin

;--------------------------------

; Pages

;Page components
;Page directory
Page instfiles

;Page custom servicePage

UninstPage uninstConfirm
UninstPage instfiles

;--------------------------------

; The stuff to install
Section "LingMEM (required)"
  
  IfFileExists $INSTDIR\*.* 0 overWriteInstall
   MessageBox MB_YESNO "W systemie jest ju¿ obecna wersja programu LingMEM. Mam kontynuowaæ mimo to?" IDYES overWriteInstall
     Quit
  overWriteInstall:

  SectionIn RO
  
  ; Set output path to the installation directory.
  SetOutPath $INSTDIR
  
  ; Put files there
  SetOverwrite On  
  File /r LingMEM  

  SetOutPath $INSTDIR\LingMEM

; Definitions for Java 1.6 Detection
!define JRE_VERSION "1.6"
!define JRE_URL "http://javadl.sun.com/webapps/download/AutoDL?BundleId=52261"
  
  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" \
             "CurrentVersion"
  StrCmp $2 ${JRE_VERSION} skipJREInstall
  StrCmp $2 "" runJREInstall  
  goto skipJREInstall 
      ; MessageBox MB_YESNO "W systemie jest ju¿ obecna Java JRE w wersji $2. Czy mam zainstalowaæ wersjê ${JRE_VERSION} mimo to?" IDYES runJREInstall
  
runJREInstall:

  Call DownloadAndInstallJRE
 ; FOR off-line Installer 
 ; Call CopyAndInstallJRE
  
skipJREInstall:

  WriteRegStr HKLM SOFTWARE\NSIS_LingMEM "Install_Dir" "$INSTDIR"
    
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\LingMEM" "DisplayName" "NSIS LingMEM"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\LingMEM" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\LingMEM" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\LingMEM" "NoRepair" 1
  WriteUninstaller "uninstall.exe"
  
SectionEnd

; Optional section (can be disabled by the user)
Section "Start Menu Shortcuts"
  CreateDirectory "$SMPROGRAMS\LingMEM"
  CreateShortCut "$SMPROGRAMS\LingMEM\LingMEM.lnk" "$INSTDIR\LingMEM\lingmem.exe" "" "$INSTDIR\LingMEM\lingmem.exe" 0
  CreateShortCut "$SMPROGRAMS\LingMEM\appconfig.properties.lnk" "$INSTDIR\LingMEM\appconfig.properties" "" "$WINDIR\notepad.exe" 0
  CreateShortCut "$SMPROGRAMS\LingMEM\LingMEM_info.rtf.lnk" "$INSTDIR\LingMEM\doc\LingMEM_info_pl.rtf" "" "$WINDIR\wordpad.exe" 0  
  CreateShortCut "$SMPROGRAMS\LingMEM\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  CreateShortCut "$DESKTOP\LingMEM.lnk" "$INSTDIR\LingMEM\lingmem.exe" "" "$INSTDIR\LingMEM\lingmem.exe" 0   
SectionEnd

;--------------------------------

; Uninstaller

Section "Uninstall"
  
  ; Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\LingMEM"
  DeleteRegKey HKLM SOFTWARE\NSIS_LingMEM

  ; Remove directories used
  RMDir /R "$SMPROGRAMS\LingMEM"
  RMDir /R "$INSTDIR"
  Delete "$DESKTOP\LingMEM.lnk"  
  
  IfFileExists $INSTDIR\*.* 0 finishDeinstall
  	MessageBox MB_OK "Deinstalacja nie powiod³a siê - proszê rêcznie usun¹æ katalog $INSTDIR "; 
    Quit
  finishDeinstall:  
SectionEnd

Function DownloadAndInstallJRE
        MessageBox MB_OK "LingMEM u¿ywa darmowego œrodowiska Java JRE w wersji ${JRE_VERSION}, zostanie ono pobrane i zainstalowane."
        StrCpy $2 "$TEMP\Java_JRE_1.6.exe"
        NSISdl::download /TIMEOUT=30000 "${JRE_URL}" "$2"
        Pop $R0 ;Get the return value
                StrCmp $R0 "success" +3
                MessageBox MB_OK "B³¹d podczas pobierania : $R0"
                Quit
        ExecWait $2
        Delete $2
FunctionEnd

Function CopyAndInstallJRE
  ;SetOutPath $TEMP
  ;File /r jre
  ;ExecWait "$TEMP\jre\java_jre_i586.exe"
  ;Delete "$TEMP\jre\java_jre_i586.exe"  
FunctionEnd
