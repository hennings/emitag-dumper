#!/usr/bin/perl

use DBI;
use DateTime::Format::Excel;
use DateTime;

$DSN="etimevideo";

#my $DSN = "driver={Microsoft Access Driver (*.mdb, *.acc)}:dbq=c:\\tyrving\\db\\070520_CC_KL\\etime.mdb";

my $dbh_2=DBI->connect("dbi:ODBC:$DSN",'admin','') or die $DBI::errstr;
$dsn_m = "DBI:mysql:database=onlineo;host=wiberg.skiinfo.com;port=3306";
my $dbh_1=DBI->connect($dsn_m,"onlineo","onlineo") or die $DBI::errstr;

my $st_all = $dbh_1->prepare("select * from o167_splittime where processed is null order by id limit 2");
my $st_upd = $dbh_1->prepare("update o167_splittime set processed=1 where id=?");

my $ste_1 = $dbh_2->prepare("select id,ecard,startno,starttime,name from name where ecard=?");
my $ste = $dbh_2->prepare("insert into mellom (id,mtime,strtid,mintime,mchanged,iplace,stasjon) values (?,?,?,?,?,?,?)"); 

$st_all->execute();

#my $a = $dbh_2->selectall_arrayref("select id,ecard,startno,starttime,name from name where ecard=480107");
#print ":: ".$a->[0]->{id}."\n";
#exit;

$|=1;
while ($h = $st_all->fetchrow_hashref()) {
  print $h->{ecardno}."\n";

  $ste_1->execute($h->{ecardno});
  my $etime_name = $ste_1->fetchrow_hashref();

  print $etime_name->{name}." - ".$etime_name->{startno}."\n";
  if ($h->{timestr}=~ /(\d*):?(\d+):(\d+)/) {
    my $dt = DateTime->new(  year=>1899,month=>12,day=>30,hour => $1, minute => $2, second=>$3 );
    $mintime = DateTime::Format::Excel->format_datetime($dt);
    $mintime = $mintime - int($mintime);
    print "start: ".$etime_name->{starttime}."\n";
    print "mintime: $mintime\n";
    $mtime = $mintime - $etime_name->{starttime};
    print "mtime: $mtime\n";
    my $mt_e = DateTime::Format::Excel->parse_datetime($mtime);
    my $strtid = $mt_e->hms;
    print "Mtime: ".$mt_e->hms."\n";
    print "Mintime: ".DateTime::Format::Excel->parse_datetime($mintime)->hms."\n";
    print "Start: ".DateTime::Format::Excel->parse_datetime($etime_name->{starttime})->hms."\n";
    my $now = DateTime::Format::Excel->format_datetime(DateTime->now);
    $now = $now-int($now);
   my $sql = "insert into mellom (id,mtime,strtid,mintime,mchanged,iplace,stasjon) values ($etime_name->{id},$mtime,'$strtid',$mintime,$now,$h->{station},$h->{station})";
    print "$sql\n";
    $dbh_2->do($sql);
    $st_upd->execute($h->{id});
#    $ste->execute($etime_name->{id},$mtime, $strtid, $mintime, $now, $h->{station}, $h->{station});
  }
}


# excelstrekktid, tekststrekktid,exceltod,exceltodts,stasjon,stasjon,box-serial

