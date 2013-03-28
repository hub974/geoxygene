package fr.ign.cogit.geoxygene.distance;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * Implementation of "An Efficiently Computable Metric for Comparing Polygonal Shapes," by Arkin, Chew, Huttenlocher,
 * Kedem, and Mitchel (undated). This expands a little on the cited reference to achieve O(n) space and O(mn log n) run
 * time.
 * <p>
 * This could be improved to O(min m,n) and O(mn log min m,n) by selecting the smallest of the 2 polys to create the
 * initial event heap. See init_events().
 * <p>
 * Variable names match the article.
 * <p>
 * Implementation (c) Eugene K. Ressler 91, 92 This source may be freely distributed and used for non-commercial
 * purposes, so long as this comment is attached to any code copied or derived from it.
 */
public class PolygonTurningSimilarity {
  /**
   * Compute floor(log_base2(x))
   */
  int ilog2(int x) {
    return new Double(Math.floor(Math.log10(x) / Math.log10(2.0))).intValue();
  }

  private int tr_i(TurnRep tr, int i) {
    return i % tr.leg.size();
  }

  private double tr_len(TurnRep tr, int i) {
    return tr.leg.get(tr_i(tr, i)).len;
  }

  private double tr_s(TurnRep tr, int i) {
    return tr.leg.get(i % tr.leg.size()).s + i / tr.leg.size();
  }

  double tr_theta(TurnRep tr, int i) {
    return tr.leg.get(i % tr.leg.size()).theta + i / tr.leg.size() * 2 * Math.PI;
  }

  /**
   * Return angle a, adjusted by +-2kPI so that it is within [base-PI, base+PI).
   */
  double turn(double a, double base) {
    while (a - base < -Math.PI) {
      a += 2 * Math.PI;
    }
    while (a - base >= Math.PI) {
      a -= 2 * Math.PI;
    }
    return a;
  }

  /**
   * Convert a polygon to a turning rep. This computes the absolute angle of each leg wrt the x-axis, then adjusts this
   * to within PI of the last leg to form the turning angle. Finally, the total length of all legs is used to compute
   * the cumulative normalized arc length of each discontinuity, s.
   * @param p a polygon
   * @return a turning rep representing the given polygon
   */
  TurnRep poly_to_turn_rep(IPolygon p) {
    TurnRep t = new TurnRep();
    int n = p.coord().size();
    double theta0 = 0;
    double total_len = 0;
    for (int i0 = 0; i0 < n - 1; ++i0) {
      /*
       * Look one vertex ahead of i0 to compute the leg.
       */
      int i1 = i0 + 1;
      double dx = p.coord().get(i1).getX() - p.coord().get(i0).getX();
      double dy = p.coord().get(i1).getY() - p.coord().get(i0).getY();
      theta0 = turn(Math.atan2(dy, dx), theta0);
      Leg l = new Leg();
      t.leg.add(l);
      l.theta = theta0;
      total_len += l.len = Math.sqrt(dx * dx + dy * dy);
    }
    t.total_len = total_len;
    double len = 0;
    for (int i0 = 0; i0 < t.leg.size(); ++i0) {
      t.leg.get(i0).s = len / total_len;
      len += t.leg.get(i0).len;
    }
    return t;
  }

  /**
   * Fill in a turn rep with a rotated version of an original. Normalized arc lengths start at 0 in the new
   * representation.
   * @param t input turn rep
   * @param to rotation / start angle
   * @param r rotated turnrep
   */
  void rotate_turn_rep(TurnRep t, int to, TurnRep r) {
    int n = t.leg.size();
    double total_len = r.total_len = t.total_len;
    for (int ti = to, ri = 0; ri < n; ++ti, ++ri) {
      Leg l = new Leg();
      r.leg.add(l);
      l.theta = tr_theta(t, ti);
      l.len = tr_len(t, ti);
      l.s = tr_s(t, ti);
    }
    double len = 0.0;
    for (int ri = 0; ri < n; ++ri) {
      r.leg.get(ri).s = len / total_len;
      len += r.leg.get(ri).len;
    }
  }

  /**
   * In one O(m + n) pass over the turning reps of the polygons to be matched, this computes all the terms needed to
   * incrementally compute the metric. See the paper.
   * @param f turning rep of the first polygon
   * @param g turning rep of the second polygon
   * @return Initialized values
   */
  InitVals init_vals(TurnRep f, TurnRep g) {
    int i, n; /* loop params */
    int fi, gi; /* disconts that bound current strip */
    double ht0, slope; /* per paper */
    double a; /* alpha in the paper */
    double last_s; /* s at left edge of current strip */
    double ds; /* width of strip */
    double dtheta; /* height of strip */
    last_s = 0;
    /*
     * First strip is between 0 and the earliest of 1'th f and g disconts.
     */
    gi = 1;
    fi = 1;
    /*
     * Zero accumulators and compute initial slope.
     */
    ht0 = a = 0;
    slope = (tr_s(g, 1) < tr_s(f, 1)) ? 0 : -Math.pow(tr_theta(g, 0) - tr_theta(f, 0), 2);
    /*
     * Count all the strips
     */
    for (i = 0, n = f.leg.size() + g.leg.size() - 1; i < n; ++i) {
      /*
       * Compute height of current strip.
       */
      dtheta = tr_theta(g, gi - 1) - tr_theta(f, fi - 1);
      /*
       * Determine flavor of discontinuity on right.
       */
      if (tr_s(f, fi) <= tr_s(g, gi)) {
        /*
         * It's f. Compute width of current strip, then bump area accumulators.
         */
        ds = tr_s(f, fi) - last_s;
        a += ds * dtheta;
        ht0 += ds * dtheta * dtheta;
        /*
         * Determine flavor of next strip. We know it's ff or fg. In latter case, bump accumulator. Note we've skipped
         * the first strip. It's added as the "next" of the last strip.
         */
        if (tr_s(f, fi + 1) > tr_s(g, gi))
          slope += Math.pow(tr_theta(f, fi) - tr_theta(g, gi - 1), 2);
        /*
         * Go to next f discontinuity.
         */
        last_s = tr_s(f, fi++);
      } else {
        /*
         * Else it's g ...
         */
        ds = tr_s(g, gi) - last_s;
        a += ds * dtheta;
        ht0 += ds * dtheta * dtheta;
        /*
         * ... and next strip is gg or gf, and again we're interested in the latter case.
         */
        if (tr_s(g, gi + 1) >= tr_s(f, fi))
          slope -= Math.pow(tr_theta(g, gi) - tr_theta(f, fi - 1), 2);
        /*
         * Go to next g discontinuity.
         */
        last_s = tr_s(g, gi++);
      }
    }
    /*
     * Set up all return values.
     */
    return new InitVals(ht0, slope, a);
  }

  /**
   * Recompute ht0 and slope for the current event. Renormalize the turning reps so that the event discontinuities are
   * first in each. This keeps all s values within [0,1) while recomputing so that all are represented with the same
   * precision. If we check that no other events are pending within machine epsilon of t for (fi,gi) before calling
   * this, numerical stability is guaranteed (unlike the first two ways I tried).
   * @param f turning rep of the first polygon
   * @param g turning rep of the second polygon
   * @param fi rotation for the first polygon
   * @param gi rotation for the second polygon
   * @return Initialized values
   */
  InitVals reinit_vals(TurnRep f, TurnRep g, int fi, int gi) {
    TurnRep fr = new TurnRep(), gr = new TurnRep();
    rotate_turn_rep(f, fi, fr);
    rotate_turn_rep(g, gi, gr);
    return init_vals(fr, gr);
  }

  /**
   * Compute number of events between successive reinits that will not blow the asymptotice complexity bound.
   * @param f turning rep of the first polygon
   * @param g turning rep of the second polygon
   * @return number of events
   */
  int reinit_interval(TurnRep f, TurnRep g) {
    return f.leg.size() * g.leg.size() / (Math.min(f.leg.size(), g.leg.size()) * ilog2(g.leg.size()));
  }

  /**
   * Following are routines to maintian the event heap. This is initialized with one event per g discontinuity, namely,
   * the one due to the f discontinuity closest to the right. The sort key is the "f shift" parameter t. As the
   * algorithm runs, it draws an event (of min t) from the heap, handles it, then inserts the event due to the *next* f
   * discontinuity associated with the same g discontinuity (unless this event would have t>1).
   * <p>
   * The heap insert and delete are minor modifications of pseudo-code from Horowitz and Sahni, Computer Algorithms.
   */
  private static Event[] event = new Event[1000];
  private static int n_events = 0;

  /**
   * Insert a new event in the heap.
   * @param f turning rep of the first polygon
   * @param g turning rep of the second polygon
   * @param fi
   * @param gi
   */
  void add_event(TurnRep f, TurnRep g, int fi, int gi) {
    double t = tr_s(f, fi) - tr_s(g, gi);
    if (t > 1) {
      return;
    }
    int j = ++n_events;
    int i = n_events / 2;
    while (i > 0 && event[i].t > t) {
      event[j] = event[i];
      j = i;
      i = i / 2;
    }
    Event e = event[j];
    if (e == null) {
      event[j] = e = new Event();
    }
    e.t = t;
    e.fi = fi;
    e.gi = gi;
  }

  /**
   * Remove the event of min t from the heap and return it.
   * @return the event of min t from the heap
   */
  Event next_event() {
    Event next = event[1]; /* remember event to return */
    Event e = event[n_events]; /* new root (before adjust) */
    --n_events;
    int i = 2;
    while (i <= n_events) {
      if (i < n_events && event[i].t > event[i + 1].t) {
        ++i;
      }
      if (e.t <= event[i].t) {
        break;
      } else {
        event[i / 2] = event[i];
        i *= 2;
      }
    }
    event[i / 2] = e;
    return (next);
  }

  /**
   * Peek at the next t to come off the heap without removing the element.
   * @return t of the next event from the heap
   */
  private double next_t() {
    return event[1].t;
  }

  /**
   * Scan the turning reps to create the initial events in the heap as described above.
   * @param f turning rep of the first polygon
   * @param g turning rep of the second polygon
   */
  void init_events(TurnRep f, TurnRep g) {
    int fi, gi;
    // for (int i = 0; i < 1000; i++) {
    // event[i] = new Event();
    // }
    n_events = 0;
    /*
     * Cycle through all g discontinuities, including the one at s = 1. This takes care of s = 0.
     */
    for (fi = gi = 1; gi <= g.leg.size(); ++gi) {
      /*
       * Look for the first f discontinuity to the right of this g discontinuity.
       */
      while (tr_s(f, fi) <= tr_s(g, gi))
        ++fi;
      add_event(f, g, fi, gi);
    }
  }

  /**
   * The heart of the algorithm: Compute the minimum value of the integral term of the metric by considering all
   * critical events. This also returns the theta* and event associated with the minimum.
   * @param f turning rep of the first polygon
   * @param g turning rep of the second polygon
   * @param hc0
   * @param slope
   * @param alpha
   * @param d_update
   * @return a set of result values including the minimum value of the integral term of the metric, the theta* and the
   *         event associated with the minimum
   */
  Result h_t0min(TurnRep f, TurnRep g, double hc0, double slope, double alpha, int d_update) {
    Integer left_to_update; /* # disconts left until update */
    Double metric2, min_metric2; /* d^2 and d^2_min thus far */
    Double theta_star, min_theta_star; /* theta* and theta*_min thus far */
    Double last_t; /* t of last iteration */
    Double hc0_err, slope_err; /* error mags discovered on update */
    Event e; /* current event */
    Event min_e; /* event of d^2_min thus far */
    Event init_min_e = new Event();
    init_min_e.fi = 0;
    init_min_e.gi = 0;
    init_min_e.t = 0.0;/* implicit first event */
    /*
     * At t = 0, theta_star is just alpha, and the min metric2 seen so far is hc0 - min_theta_star^2. Also, no error has
     * been seen.
     */
    min_theta_star = alpha;
    min_metric2 = hc0 - min_theta_star * min_theta_star;
    min_e = init_min_e;
    last_t = hc0_err = slope_err = 0.0d;
    /*
     * Compute successive hc_i0's by incremental update at critical events as described in the paper.
     */
    left_to_update = d_update;
    while (n_events > 0) {
      e = next_event();
      hc0 += (e.t - last_t) * slope;
      theta_star = alpha - 2 * Math.PI * e.t;
      metric2 = hc0 - theta_star * theta_star;
      if (metric2 < min_metric2) {
        min_metric2 = metric2;
        min_theta_star = theta_star;
        min_e = e;
      }
      /*
       * Update slope, last t, and put next event for this g discontinuity in the heap.
       */
      slope += 2 * (tr_theta(f, e.fi - 1) - tr_theta(f, e.fi)) * (tr_theta(g, e.gi - 1) - tr_theta(g, e.gi));
      last_t = e.t;
      add_event(f, g, e.fi + 1, e.gi);
      /*
       * Re-establish hc0 and slope now and then to reduce numerical error. If d_update is 0, do nothing. Note we don't
       * update if an event is close, as this causes numerical ambiguity. The test number could be much smaller, but why
       * tempt Murphey? We force an update on last event so there's always at least one.
       */
      if (d_update != 0 && (n_events == 0 || --left_to_update <= 0 && e.t - last_t > 0.001 && next_t() - e.t > 0.001)) {
        InitVals newVals = reinit_vals(f, g, e.fi, e.gi);
        double rihc0 = newVals.ht0;
        double rislope = newVals.slope;
        double dhc0 = hc0 - rihc0;
        double dslope = slope - rislope;
        if (Math.abs(dhc0) > Math.abs(hc0_err))
          hc0_err = dhc0;
        if (Math.abs(dslope) > Math.abs(slope_err))
          slope_err = dslope;
        hc0 = rihc0;
        slope = rislope;
        left_to_update = d_update;
      }
    }
    /*
     * Set up return values.
     */
    return new Result(min_metric2, min_theta_star, min_e, hc0_err, slope_err);
  }

}

class Leg { /* single leg of a turning rep polygon */
  double theta; /* heading of the leg */
  double len; /* length in original coordinates */
  double s; /* cumulative arc length in [0,1] of start */
};

class TurnRep { /* polygon in turning rep */
  // int n;
  double total_len;
  // LEG leg[MAX_PTS];
  List<Leg> leg = new ArrayList<Leg>();
};

class Event { /* critical event */
  double t; /* "f shift" parameter of the event */
  int fi, gi; /* pointers into turn reps f and g */

  void copy(Event e) {
    this.t = e.t;
    this.fi = e.fi;
    this.gi = e.gi;
  }
};

class InitVals {
  public InitVals(double ht0, double slope, double alpha) {
    this.ht0 = ht0;
    this.slope = slope;
    this.alpha = alpha;
  }

  double ht0;
  double slope;
  double alpha;
};

class Result {
  public Result(double h_t0min, double theta_star, Event e, double hc0_err, double slope_err) {
    super();
    this.h_t0min = h_t0min;
    this.theta_star = theta_star;
    this.e = e;
    this.hc0_err = hc0_err;
    this.slope_err = slope_err;
  }

  double h_t0min;
  double theta_star;
  Event e;
  double hc0_err;
  double slope_err;
};
