import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.scene.text.Text;


public class Stackulator extends Application {
  private Text display;
  private Text stackDisp;
  private IStack theStack;
  private boolean showingResult;
  private boolean radianMode;

  public Stackulator() {
    display = new Text();
    stackDisp = new Text();
    theStack = new LStack<Double>();
    showingResult = true;
    radianMode = true;
  }

  @Override
  public void start(Stage primary) {
    HBox top = new HBox();
    BorderPane bp = new BorderPane();
    GridPane gp = new GridPane();

    // number buttons are 3 wide x 4 tall
    int i = 0;
    for (char c : "789456123.0E".toCharArray()) {
      gp.add(new NumberButton("" + c), i % 3, i / 3);
      i++;
    }

    BinOpButton addButton = new BinOpButton("+", (a, b) -> a + b);
    gp.add(addButton, 3, 0);
    BinOpButton subButton = new BinOpButton("-", (a, b) -> a - b);
    gp.add(subButton, 4, 0);
    BinOpButton mulButton = new BinOpButton("*", (a, b) -> a * b);
    gp.add(mulButton, 3, 1);
    BinOpButton divButton = new BinOpButton("/", (a, b) -> a / b);
    gp.add(divButton, 4, 1);
    BinOpButton modButton = new BinOpButton("%", (a, b) -> a % b);
    gp.add(modButton, 3, 2);
    BinOpButton powButton = new BinOpButton("**", (a, b) -> Math.pow(a, b));
    gp.add(powButton, 6, 1);

    UnOpButton negButton = new UnOpButton("(-)", a -> 0-a);
    gp.add(negButton, 4, 2);
    UnOpButton lnButton = new UnOpButton("ln", a -> Math.log(a));
    gp.add(lnButton, 5, 0);
    UnOpButton logButton = new UnOpButton("log", a -> Math.log10(a));
    gp.add(logButton, 6, 0);
    UnOpButton expButton = new UnOpButton("e^x", a -> Math.exp(a));
    gp.add(expButton, 7, 0);
    UnOpButton sqrtButton = new UnOpButton("sqrt", a -> Math.sqrt(a));
    gp.add(sqrtButton, 5, 1);
    UnOpButton sinButton = new UnOpButton("sin", a -> Math.sin(radianMode ? a : Math.toRadians(a)));
    gp.add(sinButton, 5, 2);
    UnOpButton asinButton = new UnOpButton("asin", a -> radianMode ? Math.asin(a) : Math.toDegrees(Math.asin(a)));
    gp.add(asinButton, 5, 3);
    UnOpButton cosButton = new UnOpButton("cos", a -> Math.cos(a));
    gp.add(cosButton, 6, 2);
    UnOpButton acosButton = new UnOpButton("acos", a -> radianMode ? Math.acos(a) : Math.toDegrees(Math.asin(a)));
    gp.add(acosButton, 6, 3);
    UnOpButton tanButton = new UnOpButton("tan", a -> Math.tan(a));
    gp.add(tanButton, 7, 2);
    UnOpButton atanButton = new UnOpButton("atan", a -> radianMode ? Math.atan(a) : Math.toDegrees(Math.atan(a)));
    gp.add(atanButton, 7, 3);



    Button enter = new Button("enter");
    enter.setOnAction(e -> {
      Double d = getDisplayValue();
      if (!d.isNaN()) {
        theStack.push(d);
        display.setText("");
      } else {
        display.setText("ERROR");
      }
      updateStackDisp();
    });
    gp.add(enter, 3, 3, 2, 1);

    Button clear = new Button("c");
    clear.setOnAction(e -> {
      display.setText("");
      theStack.clear();
      updateStackDisp();
    });
    gp.add(clear, 5, 4);

    Button clearEntry = new Button("ce");
    clearEntry.setOnAction(e -> {
      display.setText("");
      updateStackDisp();
    });
    gp.add(clearEntry, 6, 4);

    Button backspace = new Button("<-");
    backspace.setOnAction(e -> {
      if(display.getText().length() > 0) {
        display.setText(display.getText().substring(0, display.getText().length() - 1));
      }
      showingResult = false;
    });
    gp.add(backspace, 7, 4);

    Button angleMode = new Button("-> deg");
    angleMode.setOnAction(e -> {
      radianMode = !radianMode;
      angleMode.setText(radianMode ? "-> deg" : "-> rad");
    });
    gp.add(angleMode, 3, 4);

    bp.setCenter(gp);
    bp.setTop(top);
    bp.setRight(stackDisp);
    top.getChildren().addAll(display);
    primary.setScene(new Scene(bp, 800, 500));
    primary.show();
  }

  private void updateStackDisp() {
    stackDisp.setText(theStack.toString());
  }

  private double getDisplayValue() {
    String s = display.getText();
    double d = 0;
    try {
      d = Double.parseDouble(s);
    } catch (NumberFormatException ex) {
      d = Double.NaN;
    }
    return d;
  }

  class NumberButton extends Button {
    final String symbol;
    public NumberButton(String symbol) {
      super(symbol);
      this.symbol = symbol;
      setOnAction(e -> {
        if (showingResult) {
          Double d = getDisplayValue();
          if (!d.isNaN()) {
            theStack.push(d);
          }
          display.setText("");
        }
        display.setText(display.getText() + symbol);
        showingResult = false;
      });
    }
  }

  class BinOpButton extends Button {
    private final DoubleBinaryOperator op;
    public BinOpButton(String symbol, DoubleBinaryOperator action) {
      super(symbol);
      op = action;
      this.setOnAction(e -> {
        Double second = getDisplayValue();
        if (!second.isNaN() && theStack.size() > 0) {
          double first = (Double) theStack.pop();
          Double res = op.applyAsDouble(first, second);
          display.setText(res.toString());
        } else {
          display.setText("ERROR");
        }
        updateStackDisp();
        showingResult = true;
      });
    }
  }

  class UnOpButton extends Button {
    private final DoubleUnaryOperator op;
    public UnOpButton(String symbol, DoubleUnaryOperator action) {
      super(symbol);
      op = action;
      this.setOnAction(e -> {
        Double operand = getDisplayValue();
        if (!operand.isNaN()) {
          Double res = op.applyAsDouble(operand);
          display.setText(res.toString());
        } else if (display.getText().length() > 0) {
          display.setText("ERROR");
        }
        updateStackDisp();
        showingResult = true;
      });
    }
  }
}
