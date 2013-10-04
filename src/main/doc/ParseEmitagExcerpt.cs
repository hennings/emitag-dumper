// Speed 115200 

    private void Log(emiTagCheck.LogMsgType msgtype, string msg)
    {
      bool bFound = false;
      this.rtfLog.Invoke((Delegate) delegate
      {
        while ((int) msg[0] == 10 || (int) msg[0] == 13)
          msg = msg.Substring(1, msg.Length - 1);
        while ((int) msg[msg.Length - 1] == 10 || (int) msg[msg.Length - 1] == 13)
          msg = msg.Substring(0, msg.Length - 1);
        if ((int) msg[0] == 2 && (int) msg[msg.Length - 1] == 3)
        {
          msg = msg.Substring(1, msg.Length - 1);
          msg = msg.Substring(0, msg.Length - 1);
        }
        if (msg == "PUREND")
          msg = "";
        if (msg.Length <= 0)
          return;
        if (msgtype == emiTagCheck.LogMsgType.Incoming)
        {
          foreach (string input in Regex.Split(msg, "\t"))
          {
            if (input.Length > 0)
            {
              switch (input[0])
              {
                case 'A':
                  string[] strArray1 = Regex.Split(input, "-");
                  this.toolStripProgressBarBatt.Value = (int) Convert.ToInt16(strArray1[3]);
                  this.toolStripProgressBarBatt.ToolTipText = strArray1[3] + "%";
                  this.toolStripProgressBarBatt.Style = (int) strArray1[2][0] != 43 ? ProgressBarStyle.Continuous : ProgressBarStyle.Marquee;
                  continue;
                case 'C':
                  this.C = (int) Convert.ToInt16(input.Substring(1));
                  this.toolStripStatusLabelC.Text = this.C.ToString();
                  continue;
                case 'N':
                  int N = Convert.ToInt32(input.Substring(1));
                  this.labelN.Text = N.ToString();
                  bFound = this.RegAthlete(N, (string) null);
                  if (this.emitag_program)
                  {
                    if ((Decimal) N == this.numericUpDown1.Value)
                    {
                      this.numericUpDown1.Value += this.numericUpDown2.Value;
                      this.emitag_program = false;
                    }
                    this.BackColor = Color.Empty;
                    continue;
                  }
                  else
                    continue;
                case 'Q':
                  string[] strArray2 = Regex.Split(input.Substring(1), "-");
                  if (this.C > 0 && Convert.ToInt32(strArray2[1]) == this.C)
                  {
                    long num = Convert.ToInt64(strArray2[2]) / 86400000L;
                    if (num < 5475L)
                    {
                      if (num > 2192L)
                        this.labelProdUke.BackColor = EmitVersion.red;
                      else if (num > 1827L)
                        this.labelProdUke.BackColor = EmitVersion.yellow;
                      else
                        this.labelProdUke.BackColor = Color.Empty;
                      this.labelProdUke.Text = string.Format("{0:y}", (object) DateTime.Now.AddDays((double) (num * -1L)));
                      continue;
                    }
                    else
                    {
                      this.labelProdUke.Text = "--";
                      this.labelProdUke.BackColor = Color.Empty;
                      continue;
                    }
                  }
                  else
                    continue;
                case 'R':
                  this.labelR.Text = input.Substring(1);
                  continue;
                case 'S':
                  this.labelCNR.Text = this.labelN.Text;
                  this.labelN.Text = input.Substring(1);
                  continue;
                case 'V':
                  Decimal num1 = Convert.ToDecimal(input.Substring(1, 3));
                  if (num1 <= new Decimal(27))
                  {
                    this.labelV.BackColor = EmitVersion.red;
                    this.labelV.ForeColor = Color.White;
                  }
                  else
                  {
                    this.labelV.BackColor = Color.LightGreen;
                    this.labelV.ForeColor = Color.Empty;
                  }
                  this.labelV.Text = (num1 / new Decimal(100)).ToString() + " V";
                  int num2 = Convert.ToInt32(input.Substring(5));
                  int num3 = num2 % 100;
                  num2 /= 100;
                  if (num2 >= 1 && num2 <= 53 && (num3 >= 7 && num3 <= 27))
                  {
                    this.labelProdUke.Text = num2.ToString() + "/" + num3.ToString();
                    continue;
                  }
                  else
                  {
                    this.labelProdUke.Text = "";
                    continue;
                  }
                case 'W':
                  this.dteBrikkesjekkClock = Convert.ToDateTime(input.Substring(1));
                  this.timeDiff = this.dteBrikkesjekkClock.AddMilliseconds(40.0) - DateTime.Now;
                  continue;
                case 'Y':
                  if (input.Length > 4)
                  {
                    if (input.Substring(1, 4) == "8710")
                      this.toolStripStatusLabelSN.ForeColor = Color.Blue;
                    else
                      this.toolStripStatusLabelSN.ForeColor = EmitVersion.yellow;
                    this.toolStripStatusLabelSN.Text = " " + input.Substring(1);
                    continue;
                  }
                  else
                    continue;
                default:
                  continue;
              }
            }
          }
        }
        if (!this.rtfLog.Visible)
          return;
        this.rtfLog.SelectionFont = new Font(this.rtfLog.SelectionFont, FontStyle.Bold);
        this.rtfLog.SelectionColor = this.LogMsgTypeColor[(int) msgtype];
        this.rtfLog.AppendText(msg + "\n");
        this.rtfLog.ScrollToCaret();
      });
    }
