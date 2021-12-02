#!/usr/bin/perl

"Script qui analyse les logs du web service menuCantine.
IL fait la recuperation des logs sur les portails par scp
les places dans le même répertoire que ce script, puis en fait un résumer";

@PORTAIL = qw{portail9 portail10 portail11 portail12 portail13};
$dom = '.giprecia.net';
$menuLog = 'logs/menu-cantine.log';

$idx = rindex($0, '/');

if ($idx > -1) {
	$rep=substr($0, 0, $idx) . "/";
} else {
	$rep = '';
}




$comFormat = 'scp esco@%s%s:%s %s.log'; 
foreach $p (@PORTAIL) {
	$com = sprintf($comFormat, $p, $dom, $menuLog, $rep . $p);
	print $com, "\n";
	system $com;
}

while (<$rep*.log>) {
	open LOG, $_;
	print "$_\n";
	while (<LOG>) {
		if (/WARN\s*\S+\s*\S+\s*(\d\d\:\d\d\:\d\d)\s*(.+)$/) {
			$WARN{$2}++ ;
			next;
		}
		if (/ERROR\s*\S+\s*\S+\s*(\d\d\:\d\d\:\d\d)\s* ([^\{]+)(\{)?\s+/) {
			$err = "$2 { \n";
			$err .= <LOG>;
			$err .= <LOG>;
			$err .= "}";
			$ERR{$err}++;
		}
	}
	close LOG;
}

while (($key, $val) = each %WARN) {
	print "$val warn  $key\n";
}

while (($key, $val) = each %ERR) {
	print "$val error  $key\n";
}
