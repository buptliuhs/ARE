stacksize('max');

fall=4;
moving=3;
walking=2;
shuffling=1;
standing=0;
sitting=-1;
lying=-2;
inverted=-3;
undefined=-4;

s_freq=100;

path='__SRC_FILE__';
output_file_name='__DST_FILE__';

disp("path=" + path);
disp("output_file_name=" + output_file_name);

////////////////////////////////////////

function [angle_dot,seg_val_ang,diff_angle,seg_val_ang_vel]=FN_Angle_and_AV(stat_vect,x_axis,y_axis,z_axis,s_freq)
angle_dot=FN_Angle(stat_vect,x_axis,y_axis,z_axis);

seg_cnt=floor(length(x_axis)/s_freq);
clear seg_val_ang

seg_val_ang_raw=matrix(angle_dot(1:(seg_cnt*s_freq),1),s_freq,seg_cnt);
seg_val_ang=mean(seg_val_ang_raw,1).';

//fcut_off=0.5;
//fsample=s_freq;
//order=2;
//fc = fcut_off;
//wn=(fc*2)/(fsample);
//[B,A] = butter(order,wn);
B = [241.359049041961e-006  482.718098083923e-006  241.359049041961e-006];
A = [1.00000000000000e+000   -1.95557824031504e+000  956.543676511203e-003];

angle = filter(B, A, angle_dot);
diff_angle=diff(angle);
diff_angle=diff_angle*s_freq;
diff_angle=[0;diff_angle];

clear seg_cnt_ang_vel
win=s_freq/2;
seg_cnt_ang_vel=floor(length(diff_angle)/win);

seg_val_ang_vel_raw=matrix(diff_angle(1:(seg_cnt_ang_vel*win),1),win,seg_cnt_ang_vel);
seg_val_ang_vel=mean(seg_val_ang_vel_raw,1).';

endfunction

////////////////////////////////////////

function angle_dot=FN_Angle(stat_vect,x_axis,y_axis,z_axis)

a1 = stat_vect(1,1).*ones(1,length(x_axis));
a2 = stat_vect(2,1).*ones(1,length(x_axis));
a3 = stat_vect(3,1).*ones(1,length(x_axis));

Pss_A_EX = [a1' a2' a3'];

a1=[x_axis,y_axis,z_axis].';
a2=Pss_A_EX.';
a3=sum(a1.*a2, "r");
n=a3.';

aT_UD=FN_RSS(x_axis,y_axis,z_axis);
aT_Pss=FN_RSS(Pss_A_EX(:,1),Pss_A_EX(:,2),Pss_A_EX(:,3));
d=aT_UD.*aT_Pss;
angle_dot=acos(n./d)*57.3;

endfunction

////////////////////////////////////////

function aT=FN_RSS(x_axis,y_axis,z_axis)

a1=x_axis.*x_axis;
a2=y_axis.*y_axis;
a3=z_axis.*z_axis;
aT=sqrt(a1+a2+a3);

endfunction

///////////////////////////////////

function SMA_value=FN_SMA(signal_3D,s_freq)

T_interval=s_freq;

SMA_total_down_sample=signal_3D(1:length(signal_3D(:,1)),1:3);

seg_nums=floor(length(SMA_total_down_sample(:,1))/T_interval);
SMA_value=zeros(length(seg_nums),1);

//fcut_off_low=0.75;
//fcut_off_high=15;
//fsample=100;
//order=1;
//fc1 = fcut_off_low;
//fc2 = fcut_off_high;
//w1=(fc1*2)/(fsample);
//w2=(fc2*2)/(fsample);
//wn = [w1 w2];
//[B,A] = butter(order,wn,'bandpass');
B = [0.324412497742493                   0  -0.324412497742493];
A = [1.000000000000000  -1.319111188642744   0.351175004515013];

//fcut_off_low=0.75;
//fcut_off_high=10;
//fsample=100;
//order=1;
//fc1 = fcut_off_low;
//fc2 = fcut_off_high;
//w1=(fc1*2)/(fsample);
//w2=(fc2*2)/(fsample);
//wn = [w1 w2];
//[B,A] = butter(order,wn,'bandpass');
//B = [0.230214638655737                   0  -0.230214638655737];
//A = [1.000000000000000  -1.516172417624503   0.539570722688527];

for Axis_SMA=1:3
  SMA_total_filt(:,Axis_SMA)=filter(B, A, SMA_total_down_sample(:,Axis_SMA));
  SMA_total_filt(:,Axis_SMA)=abs(SMA_total_filt(:,Axis_SMA));
end

for SMA_jmp=1:T_interval:(seg_nums*T_interval)
  SMA_segment=SMA_total_filt(SMA_jmp:(SMA_jmp+T_interval-1),1:3);
  SMA_total_sum=sum(SMA_segment,1);
  Count_total=sum(SMA_total_sum,2);
  SMA_value(1+((SMA_jmp-1)/T_interval),1)=Count_total;
end

endfunction

/////////////////////////////////////////////////////////////////////////

disp("Loading file ...");
tic();
tmp=csvRead(path);
y=toc();
disp("Loading file done: " + string(y) + " seconds");

signal_file=[tmp(:,1),tmp(:,2),tmp(:,3)];

clear fileCAL;
fileCAL(:,1)=(1:length(signal_file(:,1)))/(s_freq*60);

//f_cut_off_higher=10;
//wn=(f_cut_off_higher*2)/s_freq;
//order=2;
//[B,A] = butter(order,wn);
B = [67.4552738890719e-003  134.910547778144e-003  67.4552738890719e-003];
A = [1.00000000000000e+000   -1.14298050253990e+000  412.801598096189e-003];

disp("Filtering signal ...");
tic();
for CAL=1:3
  fileCAL(:,CAL+1)=signal_file(:,CAL);
  [fileCAL(:,CAL+1),zf]=filter(B, A, fileCAL(:,CAL+1));
end
y=toc();
disp("Filtering signal done: " + string(y) + " seconds");

x_axis=fileCAL(:,2);
y_axis=fileCAL(:,3);
z_axis=fileCAL(:,4);

signal_3D=[x_axis, y_axis, z_axis];

g_mean = [0 1 0];
Pss_A=g_mean';

stat_vect=Pss_A;

disp("Calculating SMA ...");
tic();
SMA_value=FN_SMA(signal_3D,s_freq);
SMA_value(1)=SMA_value(2);
y=toc();
disp("Calculating SMA done: " + string(y) + " seconds");

disp("Calculating Angle ...");
tic();
[angle_dot,seg_val_ang,diff_angle,seg_val_ang_vel]=FN_Angle_and_AV(stat_vect,x_axis,y_axis,z_axis,s_freq);
y=toc();
disp("Calculating Angle done: " + string(y) + " seconds");


disp("Writing files ...");
tic();
output_file = fullfile(output_file_name+'.sma');
disp("writing ... :" + output_file);
csvWrite(SMA_value, output_file);

output_file = fullfile(output_file_name+'.seg_val_ang');
disp("writing ... :" + output_file);
csvWrite(seg_val_ang, output_file);

output_file = fullfile(output_file_name+'.seg_val_ang_vel');
disp("writing ... :" + output_file);
csvWrite(seg_val_ang_vel, output_file);
y=toc();
disp("Writing files done: " + string(y) + " seconds");

exit;
