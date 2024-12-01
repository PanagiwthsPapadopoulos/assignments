function [proj] = projection(a,b, x)
    if x<=a
        proj = a; % lower bound 
    elseif x>=b
        proj = b; % upper bound
    else
        proj = x; % no need for projection
    end
end

function [trajectory] = projected_steepest_descent(f, grad_f, x0, ax,bx, ay,by, tol, sigma, gamma, max_iter)
    
    xk = x0(1);
    yk = x0(2);
    grad = grad_f(xk, yk);
    trajectory = [xk, yk, f(xk, yk)]; % Store trajectory in 3D
    k=0;    
    % MAIN ALGO IMPLEMENTATION

    while norm(grad)>tol
	
        %fprintf("#%d norm=%.6f\n",k,norm(grad));
        % 6.1.9
        x_arr_proj = projection(ax,bx, xk - sigma * grad(1));
        y_arr_proj = projection(ay,by, yk - sigma * grad(2));
        %fprintf("[x,y] = %.4f, %.4f\n" + ...
        %    "[x_mid, ymid] = %.5f, %.5f\n" + ...
        %   "[xp, yp] = %.4f, %.4f\n", xk,yk,(xk - sigma * grad(1)), (yk - sigma * grad(2)),  x_arr_proj, y_arr_proj);
        
        % 6.1.8
        xk = xk + gamma * (x_arr_proj - xk);
        yk = yk + gamma * (y_arr_proj - yk);
        %fprintf( "[x, y] = %.4f, %.4f\n\n", xk, yk);

        grad = grad_f(xk, yk);
        trajectory = [trajectory; xk, yk, f(xk, yk)];
        
        k=k+1;
        if k>max_iter
            fprintf('Did NOT Converge with constant step size in %d iterations.\n', max_iter);
            break;
        end 
    end
    if(k<max_iter)
        fprintf('Converged in %d iterations\n',k);
    end


    % PLOTTING

    % % Contour Plotting
    % figure;
    % [X, Y] = meshgrid(linspace(ax-1, bx+1, 100), linspace(ay-1, by+1, 100));
    % Z = f(X, Y);
    % contour(X, Y, Z, 20); % Plot contour lines
    % hold on;
    % plot(trajectory(:,1), trajectory(:,2), 'r-o', 'LineWidth', 2, 'MarkerSize', 5, ...
    %     'DisplayName', 'Trajectory'); % Plot trajectory
    % xlim([ax-1, bx+1]);
    % ylim([ay-1, by+1]);
    % title(sprintf('Convergence of Projected Steepest Descent\n$\\gamma = %.2f$', gamma), 'Interpreter', 'latex');
    % xlabel('$x$', 'Interpreter', 'latex');
    % ylabel('$y$', 'Interpreter', 'latex');
    % legend;
    % grid on;

    % f(xk,yk) vs k 
    figure;
    k_iter = length(trajectory(:,3));
    k = 1:k_iter;
    plot(k, trajectory(:,3), '-o');
    title(sprintf('gamma = %.1f',gamma), 'Interpreter', 'latex');
    grid on;
    xlabel("Iterations");
    ylabel('$f(x, y) = \frac{1}{3}x^2 + 3y^2$', 'Interpreter', 'latex');

    % Create a 3D surface plot of the function
    figure;
    if x0(1) > 0
        x_vals = linspace(-x0(1), x0(1), 100);
    else
        x_vals = linspace(x0(1), -x0(1), 100);
    end

    if x0(2) > 0
        y_vals = linspace(-x0(2), x0(2), 100);
    else
        y_vals = linspace(x0(2), -x0(2), 100);
    end
        
    [X, Y] = meshgrid(x_vals, y_vals);
    Z = f(X, Y);
    surf(X, Y, Z, 'EdgeColor', 'none', 'FaceAlpha', 0.9);
    hold on;
    % Plot the trajectory for the constant step size
    plot3(trajectory(:,1), trajectory(:,2), trajectory(:,3), 'r-o', ...
          'LineWidth', 2, 'MarkerSize', 5, 'DisplayName', 'Constant Step');

    % Add labels and legend
    title(sprintf('3D Trajectories from Starting Point (%.1f, %.1f)', x0(1), x0(2)));
    xlabel('$x$', 'Interpreter', 'latex');
    ylabel('$y$', 'Interpreter', 'latex');
    zlabel('$f(x, y) = \frac{1}{3}x^2 + 3y^2$', 'Interpreter', 'latex');
    legend;
    grid on;

end





clearvars; % clear workspace
clc; % clear command window

% Define the function and its gradient
f = @(x, y) 1/3*x.^2 + 3*y.^2;
grad_f = @(x, y) [2/3*x; 6*y];

% PLOTTING f(x,y)
[X, Y] = meshgrid(-2:0.1:2, -2:0.1:2);
Z = f(X, Y);
figure;
surf(X, Y, Z);
title('$f(x, y) = \frac{1}{3}x^2 + 3y^2$', 'Interpreter', 'latex');
xlabel('$x$', 'Interpreter', 'latex');
ylabel('$y$', 'Interpreter', 'latex');
zlabel('$z$', 'Interpreter', 'latex');

% Parameters for the algorithm
initial_points = [5, -5; -5,10; 8,-10];                % Starting points
tol = [0.01, 0.01, 0.01];                             % Tolerance for convergence
sigma = [5, 15, 0.1];
gamma =  [0.5, 0.1, 0.2];

max_iter = 1300;                         % Maximum iterations


%3.2
projected_steepest_descent(f, grad_f, initial_points(1,:), -10,5, -8,12, tol(1), sigma(1), gamma(1), max_iter);
%3.3
projected_steepest_descent(f, grad_f, initial_points(2,:), -10,5, -8,12, tol(2), sigma(2), gamma(2), max_iter);
%3.4
projected_steepest_descent(f, grad_f, initial_points(3,:), -10,5, -8,12, tol(3), sigma(3), gamma(3), max_iter);
