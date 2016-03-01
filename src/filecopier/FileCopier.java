/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filecopier;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Alp Sayin
 */
public class FileCopier extends JFrame implements ActionListener
{

    /**
     * @param args the command line arguments
     */
    JLabel progressLabel;
    JProgressBar progressBar;
    JButton okButton;
    public FileCopier()
    {
        super("Safe File Copier");
        progressLabel = new JLabel();
        progressBar = new JProgressBar(0, 100);
        okButton = new JButton("OK");
        okButton.setEnabled(false);
        okButton.addActionListener(this);
        setLayout(new BorderLayout());
        super.add("North", progressLabel);
        super.add("Center", progressBar);
        super.add("South", okButton);
        super.setSize(320, 100);
        super.setVisible(true);
        super.setLocation(200, 200);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void setNumberOfFiles(int numOfFiles)
    {
        progressBar.setMaximum(numOfFiles);
    }
    public void setProgress(String filename, int progress)
    {
        progressLabel.setText(filename);
        progressBar.setValue(progress);
        if(progress == progressBar.getMaximum())
            okButton.setEnabled(true);
    }
    @Override public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == okButton)
        {
            System.exit(0);
        }
    }
    public static void main(String[] args)
    {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e)
            {
                System.err.println(t.getName());
                e.printStackTrace();
            }
        });
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException ex)
        {
            Logger.getLogger(FileCopier.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex)
        {
            Logger.getLogger(FileCopier.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex)
        {
            Logger.getLogger(FileCopier.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (UnsupportedLookAndFeelException ex)
        {
            Logger.getLogger(FileCopier.class.getName()).log(Level.SEVERE, null, ex);
        }
        JFileChooser inputChooser = new JFileChooser(System.getProperty("user.home"));
        inputChooser.setMultiSelectionEnabled(true);
        inputChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int retVal = inputChooser.showOpenDialog(null);
        boolean deleteSources = false;
        if(retVal == JFileChooser.APPROVE_OPTION)
        {
            File[] selectedFiles = inputChooser.getSelectedFiles();
            retVal = JOptionPane.showConfirmDialog(null, "Should I delete the source files?", "Delete Sources?", JOptionPane.YES_NO_OPTION);
            if(retVal == JOptionPane.YES_OPTION)
                deleteSources = true;
            JFileChooser outputChooser = new JFileChooser(inputChooser.getCurrentDirectory());
            outputChooser.setMultiSelectionEnabled(false);
            outputChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            retVal = outputChooser.showSaveDialog(null);
            if(retVal == JFileChooser.APPROVE_OPTION)
            {
                File outputDir = outputChooser.getSelectedFile();
                FileCopier fc = new FileCopier();
                fc.setNumberOfFiles(selectedFiles.length);
                int progress = 1;
                for(File f : selectedFiles)
                {
                    FileOutputStream fos = null;
                    FileInputStream fis = null;
                    try
                    {
                        File outputFile = new File(outputDir.getAbsolutePath()+File.separator+f.getName());
                        int extension = 0;
                        while(outputFile.exists())
                        {
                            String filename = f.getName();
                            filename = filename.substring(0, filename.lastIndexOf(".")) + extension + filename.substring(filename.lastIndexOf("."));
                            outputFile = new File(outputDir.getAbsolutePath()+File.separator+filename);
                            extension++;
                        }
                        fos = new FileOutputStream(outputFile);
                        fis = new FileInputStream(f);
                        byte[] buffer = new byte[8192];
                        for(int writtenLength=0; writtenLength<f.length(); )
                        {
                            int tmpLength = fis.read(buffer);
                            fos.write(buffer, 0, tmpLength);
                            writtenLength+=tmpLength;
                        }
                        fis.close();
                        fos.close();
                        if(deleteSources)
                            f.deleteOnExit();
                        fc.setProgress(f.getAbsolutePath(), progress++);
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(FileCopier.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
