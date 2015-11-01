sit_stand_th_angle=15;
sit_lie_th_angle=80;
lie_invert_th_angle=140;

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

//fall=7;
//moving=6;
//walking=5;
//shuffling=4;
//standing=3;
//sitting=2;
//lying=1;
//inverted=0;
//undefined=-1;

angular_vel_rotation=14;
static_dynamic_TH=5;
f_cut_off_higher=10;
WalkingTimeThreshold=5;
s_freq=100;

//path='D:\usense\test\ID099_16_1_2015_8_34_UoA002_conv.csv';
//output_file_name="D:\data.csv";

path='__SRC_FILE__';
output_file_name='__DST_FILE__';

disp("path=" + path);
disp("output_file_name=" + output_file_name);
////////////////////////////////
//function [Pos_num,Pos_act_short]=FN_torso_angle(angle_val)
function [Pos_num]=FN_torso_angle(angle_val)

if(angle_val<=sit_stand_th_angle)
  Pos_num=standing;
  //Pos_act_short='Stnd';
elseif(angle_val>sit_stand_th_angle & angle_val<=sit_lie_th_angle)
  Pos_num=sitting;
  //Pos_act_short='Sitt';
elseif(angle_val>sit_lie_th_angle & angle_val<=lie_invert_th_angle)
  Pos_num=lying;
  //Pos_act_short='Lyng';
elseif(angle_val>lie_invert_th_angle)
  Pos_num=inverted;
  //Pos_act_short='Invt';
end
endfunction

////////////////////////////////////////

function [angle_dot,seg_val_ang,diff_angle,seg_val_ang_vel]=FN_Angle_and_AV(stat_vect,x_axis,y_axis,z_axis,s_freq)
angle_dot=FN_dot_product_angle(stat_vect,x_axis,y_axis,z_axis);

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

function angle_dot=FN_dot_product_angle(stat_vect,x_axis,y_axis,z_axis)

a1 = stat_vect(1,1).*ones(1,length(x_axis));
a2 = stat_vect(2,1).*ones(1,length(x_axis));
a3 = stat_vect(3,1).*ones(1,length(x_axis));

Pss_A_EX = [a1' a2' a3'];

//numer=(dot([x_axis,y_axis,z_axis].',Pss_A_EX.').');

a1=[x_axis,y_axis,z_axis].';
a2=Pss_A_EX.';
a3=sum(a1.*a2, "r");
n=a3.';

aT_UD=SquareSumSqrt(x_axis,y_axis,z_axis);
aT_Pss=SquareSumSqrt(Pss_A_EX(:,1),Pss_A_EX(:,2),Pss_A_EX(:,3));
d=aT_UD.*aT_Pss;
angle_dot=acos(n./d)*57.3;

endfunction

////////////////////////////////////////

function aT=SquareSumSqrt(x_axis,y_axis,z_axis)

a1=x_axis.*x_axis;
a2=y_axis.*y_axis;
a3=z_axis.*z_axis;
aT=sqrt(a1+a2+a3);

endfunction

///////////////////////////////////

function IMA_value=FN_Energy_Expenditure(EE_total,sample_f)

T_interval=sample_f;

EE_total_down_sample=EE_total(1:length(EE_total(:,1)),1:3);

seg_nums=floor(length(EE_total_down_sample(:,1))/T_interval);
IMA_value=zeros(length(seg_nums),1);

//fcut_off_low=0.75;
//fcut_off_high=15;
//fsample=sample_f;
//order=1;
//fc1 = fcut_off_low;
//fc2 = fcut_off_high;
//w1=(fc1*2)/(fsample);
//w2=(fc2*2)/(fsample);
//wn = [w1 w2];
//[B,A] = butter(order,wn,'bandpass');

B = [324.412497742493e-003  0.00000000000000e+000   -324.412497742493e-003];
A = [1.00000000000000e+000   -1.31911118864274e+000  351.175004515013e-003];

for Axis_EE=1:3
  EE_total_filt(:,Axis_EE)=filter(B, A, EE_total_down_sample(:,Axis_EE));
  EE_total_filt(:,Axis_EE)=abs(EE_total_filt(:,Axis_EE));
end

for EE_jmp=1:T_interval:(seg_nums*T_interval)
  EE_segment=EE_total_filt(EE_jmp:(EE_jmp+T_interval-1),1:3);
  EE_total_sum=sum(EE_segment,1);
  Count_total=sum(EE_total_sum,2);
  IMA_value(1+((EE_jmp-1)/T_interval),1)=Count_total;
end

endfunction

/////////////////////////////////////////////////////////////////////////

tmp=csvRead(path);

signal_file=[tmp(:,2),tmp(:,3),tmp(:,4)];

clear fileCAL;
fileCAL(:,1)=(1:length(signal_file(:,1)))/(s_freq*60);

//wn=(f_cut_off_higher*2)/s_freq;
//order=2;
//[B,A] = butter(order,wn);

B = [67.4552738890719e-003  134.910547778144e-003  67.4552738890719e-003];
A = [1.00000000000000e+000   -1.14298050253990e+000  412.801598096189e-003];

for CAL=1:3
  fileCAL(:,CAL+1)=signal_file(:,CAL);
  [fileCAL(:,CAL+1),zf]=filter(B, A, fileCAL(:,CAL+1));
end

x_axis=fileCAL(:,2);
y_axis=fileCAL(:,3);
z_axis=fileCAL(:,4);

signal_3D=[x_axis, y_axis, z_axis];

g_mean = [0 1 0];
Pss_A=g_mean';

stat_vect=Pss_A;

threshold_stand_sit=angular_vel_rotation;
threshold_sit_stand=-angular_vel_rotation;

IMA_value=FN_Energy_Expenditure(signal_3D,s_freq);
IMA_value(1)=IMA_value(2);

[angle_dot,seg_val_ang,diff_angle,seg_val_ang_vel]=FN_Angle_and_AV(stat_vect,x_axis,y_axis,z_axis,s_freq);

//////////////////////////////////////////////////////////////////////

Tran_Stand_SitLie=0;
Tran_SitLie_Stand=0;
Transition=0;
move_t_cnt=0;
active_flag_first_time=1;

////
//// The activity classification algorithm is here
seg_cnt=floor(length(x_axis)/s_freq);
clear p;
p.num=undefined;
p.num_filt=undefined;
//p.short='Undf';
//p.short_filt='Undf';
clear Pos;
Pos(1:seg_cnt, 1) = p;
step=seg_cnt/20;
i_step = 0;
for j=1:seg_cnt  // The activity classification algorithm is here
  if(j - 1 >= (i_step * step))
    i_step=i_step+1;
    disp(string(int16(100*(j-1)/seg_cnt)) + " %");
  end
  if(j==1)
    Pos(j,1).num=undefined;
    Pos(j,1).num_filt=undefined;
    //Pos(j,1).short='Undf';
    //Pos(j,1).short_filt='Undf';

    //// Update the activity variable with the last static activity if no
    //// transition has occured
    //[num,short]=FN_torso_angle(seg_val_ang(j));
    [num]=FN_torso_angle(seg_val_ang(j));
    last_static_activity_num=num;
    //last_static_activity_sht=short;
  elseif(j>1) // The current state is equal to the last previous state
    Pos(j,1).num=Pos(j-1,1).num;
    Pos(j,1).num_filt= Pos(j-1,1).num_filt;
    //Pos(j,1).short=Pos(j-1,1).short;// The previous state;
    //Pos(j,1).short_filt=Pos(j-1,1).short_filt;// The previous state;
  end
  
  if(IMA_value(j)<=static_dynamic_TH)  //// Static activity
    active_flag_first_time=1;

    move_t_cnt=0; // movement counter is reset each time a period of static activity is detected

    if(Transition==1) // A Transition was detected
      Transition=0;

      //[Pos(j,1).num,Pos(j,1).short]=FN_torso_angle(seg_val_ang(j));
      [Pos(j,1).num]=FN_torso_angle(seg_val_ang(j));
      Pos(j,1).num_filt=Pos(j,1).num;
      //Pos(j,1).short_filt=Pos(j,1).short;

      //// Update the activity variable with the last static activity if no
      //// transition has occured
      last_static_activity_num=Pos(j,1).num;
      //last_static_activity_sht=Pos(j,1).short;
    elseif(Transition==0)
      //// Update the activity variable with the last static activity if no
      //// transition has occured
      Pos(j,1).num=last_static_activity_num;
      //Pos(j,1).short=last_static_activity_sht;

      Pos(j,1).num_filt=last_static_activity_num;
      //Pos(j,1).short_filt=last_static_activity_sht;
    end
  elseif(IMA_value(j)>static_dynamic_TH)  // Dynamic activity
    if(active_flag_first_time==1) // Start of dynamic activity
      active_flag_first_time=0;

      Pos(j,1).num=moving;
      Pos(j,1).num_filt=last_static_activity_num; //Movement is detected but "_filt" is going to ignore the movement category

      //Pos(j,1).short='Move';
      //Pos(j,1).short_filt=last_static_activity_sht; //Movement is detected but "_filt" is going to ignore the movement category
    end
    //// Need to add in lines here to say if moving and if angular
    //// velocity is greater that movement threshold then transition
    //threshold_stand_sit > 0
    //threshold_sit_stand < 0
    //angular_vel_rotation = threshold_stand_sit = 14
    if(seg_val_ang_vel(j*2)>threshold_stand_sit | seg_val_ang_vel((j*2)-1)>threshold_stand_sit)
      // Transition from standing to sitting/lying
      Transition=1;
      Tran_Stand_SitLie=1;
      Pos(j,1).num=lying;
      //Pos(j,1).short='Tran';   //'StSi';      //'Stand to Sit/lie, ';
      move_t_cnt=2; // Resets the movement count back 2 seconds
      walking_flag=0;
    elseif(seg_val_ang_vel(j*2)<threshold_sit_stand | seg_val_ang_vel((j*2)-1)<threshold_sit_stand)
      // Transition from sitting/lying to standing
      Transition=1;
      Tran_SitLie_Stand=1;
      Pos(j,1).num=sitting;
      //Pos(j,1).short='Tran';   //'Sit/lie to Stand, ';
      move_t_cnt=2; // Resets the movement count back 2 seconds
      walking_flag=0;
    elseif(seg_val_ang_vel(j*2)<threshold_stand_sit & seg_val_ang_vel((j*2)-1)<threshold_stand_sit)
      // Walking
      move_t_cnt=move_t_cnt+1;
      if(move_t_cnt>=WalkingTimeThreshold) //// If moving is greater than WalkingTimeThreshold seconds it is walking
        Pos(j,1).num=walking;
        //Pos(j,1).short='Walk';
        if(walking_flag==0)
          for k=j:-1:j-(WalkingTimeThreshold-1)       // Updates the last 5 previous activites to be walking also
            //Pos(k,1).short_filt='Walk';
            Pos(k,1).num_filt=walking;
          end
        elseif(walking_flag==1)
          Pos(j,1).num_filt=walking;
          //Pos(j,1).short_filt='Walk';
        end
        walking_flag=1;

        //// Updates the last static orientation
        //[num,short]=FN_torso_angle(seg_val_ang(j));
        [num]=FN_torso_angle(seg_val_ang(j));
        last_static_activity_num=num;
        //last_static_activity_sht=short;
      elseif(move_t_cnt<WalkingTimeThreshold)
        Pos(j,1).num=moving;
        Pos(j,1).num_filt=last_static_activity_num; //Movement is detected but "_filt" is going to ignore the movement category

        //Pos(j,1).short='Move';
        //Pos(j,1).short_filt=last_static_activity_sht; //Movement is detected but "_filt" is going to ignore the movement category
        walking_flag=0;

        if(last_static_activity_num==standing) // If the last static state was standing and now there is movement
          Pos(j,1).num=shuffling;
          Pos(j,1).num_filt=shuffling;
          //Pos(j,1).short='Shuf';
          //Pos(j,1).short_filt='Shuf';
        end
      end
    else
      Pos(j,1).num=moving;
      Pos(j,1).num_filt=last_static_activity_num; //Movement is detected but "_filt" is going to ignore the movement category
      //Pos(j,1).short='Move';
      //Pos(j,1).short_filt=last_static_activity_sht; //Movement is detected but "_filt" is going to ignore the movement category
      walking_flag=0;
    end
  end
end
disp("100 %");

clear data_file;
for l=1:seg_cnt
  data_file(l,1)=l-1;
  data_file(l,2)=Pos(l,1).num_filt;
  //data_file(l,2)=Pos(l,1).num;
end

//////////////////////////////////////////////////////////////////////

output_file = fullfile(output_file_name);
csvWrite(data_file, output_file);

exit;
