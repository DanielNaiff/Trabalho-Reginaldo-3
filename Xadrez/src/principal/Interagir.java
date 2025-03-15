package principal;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Interagir extends MouseAdapter {

    public boolean clicou;
    public int x;
    public  int y;

    @Override
    public void mouseDragged(MouseEvent e){
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e){
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mousePressed(MouseEvent e){
        clicou = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        clicou = false;

    }
}
